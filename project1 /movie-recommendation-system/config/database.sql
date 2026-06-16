-- Create Database
CREATE DATABASE IF NOT EXISTS movie_db;
USE movie_db;

-- Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Movies Table
CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT,
    director VARCHAR(100),
    poster VARCHAR(500),
    average_rating DECIMAL(3, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Movie Genres Table (for many-to-many relationship)
CREATE TABLE IF NOT EXISTS movie_genres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    movie_id INT NOT NULL,
    genre VARCHAR(50) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Create Ratings Table (User ratings)
CREATE TABLE IF NOT EXISTS ratings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_movie (user_id, movie_id)
);

-- Create Watchlist Table
CREATE TABLE IF NOT EXISTS watchlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    movie_id INT NOT NULL,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY unique_watchlist (user_id, movie_id)
);

-- Insert Sample Movies
INSERT INTO movies (title, description, release_year, director, poster, average_rating) VALUES
('Inception', 'A mind-bending thriller about dreams and reality.', 2010, 'Christopher Nolan', 'https://m.media-amazon.com/images/I/912AErFSBHL._AC_UF894,1000_QL80_.jpg', 8.8),
('The Dark Knight', 'Batman faces his greatest challenge against the Joker.', 2008, 'Christopher Nolan', 'https://m.media-amazon.com/images/I/81PGnTe44yL._AC_UF894,1000_QL80_.jpg', 9.0),
('Pulp Fiction', 'Multiple interconnected stories in Los Angeles.', 1994, 'Quentin Tarantino', 'https://m.media-amazon.com/images/I/81z1A2-9a-L._AC_UF894,1000_QL80_.jpg', 8.9),
('Forrest Gump', 'A simple man achieves extraordinary things.', 1994, 'Robert Zemeckis', 'https://m.media-amazon.com/images/I/71h+dO322tL._AC_UF894,1000_QL80_.jpg', 8.8),
('The Matrix', 'A hacker discovers the truth about reality.', 1999, 'Wachowski Sisters', 'https://m.media-amazon.com/images/I/71P05l-bF+L._AC_UF894,1000_QL80_.jpg', 8.7),
('The Godfather', 'An organized crime dynasty transfers control.', 1972, 'Francis Ford Coppola', 'https://m.media-amazon.com/images/I/714ZOEiVN2L._AC_UF894,1000_QL80_.jpg', 9.2),
('The Shawshank Redemption', 'Two men find redemption through acts of decency.', 1994, 'Frank Darabont', 'https://m.media-amazon.com/images/I/71715eI-F-L._AC_UF894,1000_QL80_.jpg', 9.3),
('Fight Club', 'An underground fight club evolves into something more.', 1999, 'David Fincher', 'https://m.media-amazon.com/images/I/81D+KJkO4SL._AC_UF894,1000_QL80_.jpg', 8.8),
('Goodfellas', 'The life of Henry Hill in the mob.', 1990, 'Martin Scorsese', 'https://m.media-amazon.com/images/I/71w+4a54nLL._AC_UF894,1000_QL80_.jpg', 8.7),
('The Lord of the Rings: The Fellowship of the Ring', 'A journey to destroy the One Ring.', 2001, 'Peter Jackson', 'https://m.media-amazon.com/images/I/81EBp0vOZZL._AC_UF894,1000_QL80_.jpg', 8.8),
('Parasite', 'A symbiotic relationship between two families.', 2019, 'Bong Joon Ho', 'https://m.media-amazon.com/images/I/91Sru-M1i0L._AC_UF894,1000_QL80_.jpg', 8.6),
('Interstellar', 'A team explores a wormhole to save humanity.', 2014, 'Christopher Nolan', 'https://m.media-amazon.com/images/I/91kFYg4fX3L._AC_UF894,1000_QL80_.jpg', 8.6),
('The Silence of the Lambs', 'An FBI cadet seeks help from a cannibal killer.', 1991, 'Jonathan Demme', 'https://m.media-amazon.com/images/I/81SVDO6s0bL._AC_UF894,1000_QL80_.jpg', 8.6),
('Saving Private Ryan', 'Soldiers go behind enemy lines in WWII.', 1998, 'Steven Spielberg', 'https://m.media-amazon.com/images/I/812l-0r-yIL._AC_UF894,1000_QL80_.jpg', 8.6),
('Gladiator', 'A Roman general seeks vengeance.', 2000, 'Ridley Scott', 'https://m.media-amazon.com/images/I/71pA0e-A-ML._AC_UF894,1000_QL80_.jpg', 8.5),
('Joker', 'A mentally troubled comedian descends into madness.', 2019, 'Todd Phillips', 'https://m.media-amazon.com/images/I/71H4e7aP6FL._AC_UF894,1000_QL80_.jpg', 8.4),
('Whiplash', 'A young drummer is mentored by a demanding instructor.', 2014, 'Damien Chazelle', 'https://m.media-amazon.com/images/I/81mD+2v-a-L._AC_UF894,1000_QL80_.jpg', 8.5),
('The Departed', 'An undercover cop and a mole in the police.', 2006, 'Martin Scorsese', 'https://m.media-amazon.com/images/I/81mXw0w-p7L._AC_UF894,1000_QL80_.jpg', 8.5),
('The Prestige', 'Two magicians engage in a battle for supremacy.', 2006, 'Christopher Nolan', 'https://m.media-amazon.com/images/I/81n5-I+sTFL._AC_UF894,1000_QL80_.jpg', 8.5),
('Avengers: Endgame', 'Avengers face off against Thanos.', 2019, 'Anthony Russo, Joe Russo', 'https://m.media-amazon.com/images/I/81ExhpBEbHL._AC_UF894,1000_QL80_.jpg', 8.4);

-- Insert Movie Genres
INSERT INTO movie_genres (movie_id, genre) VALUES
(1, 'Action'), (1, 'Sci-Fi'), (1, 'Thriller'),
(2, 'Action'), (2, 'Crime'), (2, 'Drama'),
(3, 'Crime'), (3, 'Drama'),
(4, 'Comedy'), (4, 'Drama'), (4, 'Romance'),
(5, 'Action'), (5, 'Sci-Fi'),
(6, 'Crime'), (6, 'Drama'),
(7, 'Drama'),
(8, 'Drama'),
(9, 'Biography'), (9, 'Crime'), (9, 'Drama'),
(10, 'Action'), (10, 'Adventure'), (10, 'Drama'),
(11, 'Comedy'), (11, 'Drama'), (11, 'Thriller'),
(12, 'Adventure'), (12, 'Drama'), (12, 'Sci-Fi'),
(13, 'Crime'), (13, 'Drama'), (13, 'Thriller'),
(14, 'Drama'), (14, 'War'),
(15, 'Action'), (15, 'Adventure'), (15, 'Drama'),
(16, 'Crime'), (16, 'Drama'), (16, 'Thriller'),
(17, 'Drama'), (17, 'Music'),
(18, 'Crime'), (18, 'Drama'), (18, 'Thriller'),
(19, 'Drama'), (19, 'Mystery'), (19, 'Sci-Fi'),
(20, 'Action'), (20, 'Adventure'), (20, 'Drama');