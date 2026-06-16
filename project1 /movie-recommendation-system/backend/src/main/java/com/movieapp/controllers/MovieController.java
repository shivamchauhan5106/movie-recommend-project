
import com.movieapp.models.Movie;
import com.movieapp.models.Rating;
import com.movieapp.models.User;
import com.movieapp.repositories.MovieRepository;
import com.movieapp.repositories.RatingRepository;
import com.movieapp.repositories.UserRepository;
import com.movieapp.services.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all movies
    @GetMapping
    public ResponseEntity<?> getAllMovies(
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Movie> movies = movieRepository.findAll();
        return ResponseEntity.ok(movies);
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Search movies
    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(@RequestParam String q) {
        List<Movie> results = movieRepository.findByTitleContainingIgnoreCase(q);
        return ResponseEntity.ok(results);
    }

    // Get recommendations (hybrid approach)
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<?> getRecommendations(@PathVariable Long userId) {
        List<Movie> recommendations = recommendationService.getHybridRecommendations(userId, 10);
        return ResponseEntity.ok(recommendations);
    }

    // Rate a movie
    @PostMapping("/rate")
    public ResponseEntity<?> rateMovie(
            @RequestParam Long userId,
            @RequestParam Long movieId,
            @RequestParam Integer rating) {
        try {
            Optional<User> user = userRepository.findById(userId);
            Optional<Movie> movie = movieRepository.findById(movieId);

            if (!user.isPresent() || !movie.isPresent()) {
                return ResponseEntity.badRequest().body("User or Movie not found");
            }

            // Check if rating already exists
            Optional<Rating> existingRating = ratingRepository.findByUserAndMovie(user.get(), movie.get());
            Rating newRating;

            if (existingRating.isPresent()) {
                newRating = existingRating.get();
                newRating.setRating(rating);
            } else {
                newRating = new Rating(user.get(), movie.get(), rating);
            }

            ratingRepository.save(newRating);
            recommendationService.updateMovieRating(movie.get());

            return ResponseEntity.ok(Map.of("message", "Rating saved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get user ratings
    @GetMapping("/ratings/{userId}")
    public ResponseEntity<?> getUserRatings(@PathVariable Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Rating> ratings = ratingRepository.findByUser(user.get());
        return ResponseEntity.ok(ratings);
    }
}