package main.java.com.movieapp.repositories;

import com.movieapp.models.Rating;
import com.movieapp.models.User;
import com.movieapp.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUser(User user);

    List<Rating> findByMovie(Movie movie);

    Optional<Rating> findByUserAndMovie(User user, Movie movie);
}
