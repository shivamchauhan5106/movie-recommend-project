
import com.movieapp.models.*;
import com.movieapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * CONTENT-BASED FILTERING
     * Recommends movies with similar genres to those the user rated highly
     */
    public List<Movie> getContentBasedRecommendations(Long userId, int limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return Collections.emptyList();

        // Get user's high ratings (4-5 stars)
        List<Rating> highRatings = ratingRepository.findByUser(user).stream()
                .filter(r -> r.getRating() >= 4)
                .collect(Collectors.toList());

        if (highRatings.isEmpty()) {
            return movieRepository.findAll().stream().limit(limit).collect(Collectors.toList());
        }

        // Extract genres from highly-rated movies
        Map<String, Integer> genreScores = new HashMap<>();
        for (Rating rating : highRatings) {
            for (MovieGenre mg : rating.getMovie().getGenres()) {
                genreScores.put(mg.getGenre(), genreScores.getOrDefault(mg.getGenre(), 0) + 1);
            }
        }

        // Get IDs of already-rated movies
        Set<Long> ratedMovieIds = highRatings.stream()
                .map(r -> r.getMovie().getId())
                .collect(Collectors.toSet());

        // Score unrated movies by genre similarity
        List<Movie> recommendations = movieRepository.findAll().stream()
                .filter(m -> !ratedMovieIds.contains(m.getId()))
                .map(movie -> {
                    double score = 0;
                    for (MovieGenre mg : movie.getGenres()) {
                        score += genreScores.getOrDefault(mg.getGenre(), 0);
                    }
                    score += movie.getAverageRating();
                    return new AbstractMap.SimpleEntry<>(movie, score);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * COLLABORATIVE FILTERING
     * Recommends movies liked by similar users
     */
    public List<Movie> getCollaborativeRecommendations(Long userId, int limit) {
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null)
            return Collections.emptyList();

        // Get current user's ratings
        List<Rating> userRatings = ratingRepository.findByUser(currentUser);
        if (userRatings.isEmpty()) {
            return movieRepository.findAll().stream().limit(limit).collect(Collectors.toList());
        }

        // Create rating vector for current user
        Map<Long, Integer> userVector = userRatings.stream()
                .collect(Collectors.toMap(r -> r.getMovie().getId(), Rating::getRating));

        // Find similar users using cosine similarity
        List<User> allUsers = userRepository.findAll();
        Map<User, Double> userSimilarities = new HashMap<>();

        for (User otherUser : allUsers) {
            if (otherUser.getId().equals(userId))
                continue;

            List<Rating> otherUserRatings = ratingRepository.findByUser(otherUser);
            if (otherUserRatings.isEmpty())
                continue;

            Map<Long, Integer> otherVector = otherUserRatings.stream()
                    .collect(Collectors.toMap(r -> r.getMovie().getId(), Rating::getRating));

            double similarity = calculateCosineSimilarity(userVector, otherVector);
            if (similarity > 0.5) {
                userSimilarities.put(otherUser, similarity);
            }
        }

        if (userSimilarities.isEmpty()) {
            return movieRepository.findAll().stream().limit(limit).collect(Collectors.toList());
        }

        // Aggregate ratings from similar users
        Map<Long, Double> predictions = new HashMap<>();
        Map<Long, Double> weights = new HashMap<>();

        for (Map.Entry<User, Double> entry : userSimilarities.entrySet()) {
            User similarUser = entry.getKey();
            Double similarity = entry.getValue();

            List<Rating> similarUserRatings = ratingRepository.findByUser(similarUser);
            for (Rating rating : similarUserRatings) {
                Long movieId = rating.getMovie().getId();

                // Skip if user already rated this movie
                if (userVector.containsKey(movieId))
                    continue;

                predictions.put(movieId, predictions.getOrDefault(movieId, 0.0) + rating.getRating() * similarity);
                weights.put(movieId, weights.getOrDefault(movieId, 0.0) + similarity);
            }
        }

        // Normalize predictions
        Map<Long, Double> normalizedPredictions = new HashMap<>();
        for (Map.Entry<Long, Double> entry : predictions.entrySet()) {
            normalizedPredictions.put(entry.getKey(), entry.getValue() / weights.get(entry.getKey()));
        }

        // Get top recommended movies
        List<Movie> recommendations = normalizedPredictions.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> movieRepository.findById(e.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return recommendations;
    }

    /**
     * HYBRID RECOMMENDATION
     * Combines content-based and collaborative filtering
     */
    public List<Movie> getHybridRecommendations(Long userId, int limit) {
        List<Movie> contentBased = getContentBasedRecommendations(userId, limit * 2);
        List<Movie> collaborative = getCollaborativeRecommendations(userId, limit * 2);

        // Combine and deduplicate
        Map<Long, Integer> scores = new HashMap<>();
        for (int i = 0; i < contentBased.size(); i++) {
            scores.put(contentBased.get(i).getId(),
                    scores.getOrDefault(contentBased.get(i).getId(), 0) + (limit * 2 - i));
        }
        for (int i = 0; i < collaborative.size(); i++) {
            scores.put(collaborative.get(i).getId(),
                    scores.getOrDefault(collaborative.get(i).getId(), 0) + (limit * 2 - i));
        }

        return scores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> movieRepository.findById(e.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Calculate cosine similarity between two rating vectors
     */
    private double calculateCosineSimilarity(Map<Long, Integer> vector1, Map<Long, Integer> vector2) {
        Set<Long> keys = new HashSet<>();
        keys.addAll(vector1.keySet());
        keys.addAll(vector2.keySet());

        double dotProduct = 0;
        double magnitude1 = 0;
        double magnitude2 = 0;

        for (Long key : keys) {
            int v1 = vector1.getOrDefault(key, 0);
            int v2 = vector2.getOrDefault(key, 0);

            dotProduct += v1 * v2;
            magnitude1 += v1 * v1;
            magnitude2 += v2 * v2;
        }

        if (magnitude1 == 0 || magnitude2 == 0)
            return 0;

        return dotProduct / (Math.sqrt(magnitude1) * Math.sqrt(magnitude2));
    }

    /**
     * Get recommendations by genre
     */
    public List<Movie> getRecommendationsByGenre(Long userId, String genre, int limit) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return Collections.emptyList();

        List<Rating> userRatings = ratingRepository.findByUser(user);
        Set<Long> ratedMovieIds = userRatings.stream()
                .map(r -> r.getMovie().getId())
                .collect(Collectors.toSet());

        return movieRepository.findAll().stream()
                .filter(m -> !ratedMovieIds.contains(m.getId()))
                .filter(m -> m.getGenres().stream().anyMatch(mg -> mg.getGenre().equalsIgnoreCase(genre)))
                .sorted(Comparator.comparingDouble(Movie::getAverageRating).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Update average ratings for a movie
     */
    public void updateMovieRating(Movie movie) {
        if (movie.getRatings().isEmpty()) {
            movie.setAverageRating(0.0);
        } else {
            double avg = movie.getRatings().stream()
                    .mapToInt(Rating::getRating)
                    .average()
                    .orElse(0.0);
            movie.setAverageRating(Math.round(avg * 10.0) / 10.0);
        }
        movieRepository.save(movie);
    }
}