// frontend/js/api.js

class API {
    constructor() {
        this.baseURL = 'http://localhost:8080/api';
        this.token = localStorage.getItem('authToken');
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers,
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        try {
            const response = await fetch(url, {
                ...options,
                headers,
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // User endpoints
    signup(data) {
        return this.request('/auth/signup', {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    login(email, password) {
        return this.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password }),
        });
    }

    logout() {
        localStorage.removeItem('authToken');
        this.token = null;
    }

    // Movie endpoints
    getMovies(filters = {}) {
        const params = new URLSearchParams(filters);
        return this.request(`/movies?${params}`);
    }

    getMovieById(id) {
        return this.request(`/movies/${id}`);
    }

    searchMovies(query) {
        return this.request(`/movies/search?q=${encodeURIComponent(query)}`);
    }

    // Rating endpoints
    rateMovie(movieId, rating) {
        return this.request('/ratings', {
            method: 'POST',
            body: JSON.stringify({ movieId, rating }),
        });
    }

    getUserRatings() {
        return this.request('/ratings/user');
    }

    // Recommendation endpoints
    getRecommendations() {
        return this.request('/recommendations');
    }

    getRecommendationsByGenre(genre) {
        return this.request(`/recommendations/genre/${genre}`);
    }

    // Watchlist endpoints
    addToWatchlist(movieId) {
        return this.request('/watchlist', {
            method: 'POST',
            body: JSON.stringify({ movieId }),
        });
    }

    getWatchlist() {
        return this.request('/watchlist');
    }

    removeFromWatchlist(movieId) {
        return this.request(`/watchlist/${movieId}`, {
            method: 'DELETE',
        });
    }
}

const api = new API();