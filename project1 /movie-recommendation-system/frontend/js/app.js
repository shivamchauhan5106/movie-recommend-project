// This file contains the main JavaScript code for handling user interface interactions and managing the flow of the movie recommendation application.

document.addEventListener('DOMContentLoaded', () => {
    const recommendationButton = document.getElementById('get-recommendations');
    const recommendationsContainer = document.getElementById('recommendations');

    recommendationButton.addEventListener('click', () => {
        fetchRecommendations();
    });

    function fetchRecommendations() {
        fetch('/api/recommendations')
            .then(response => response.json())
            .then(data => {
                displayRecommendations(data);
            })
            .catch(error => {
                console.error('Error fetching recommendations:', error);
            });
    }

    function displayRecommendations(recommendations) {
        recommendationsContainer.innerHTML = '';
        recommendations.forEach(movie => {
            const movieElement = document.createElement('div');
            movieElement.classList.add('movie');
            movieElement.innerHTML = `
                <h3>${movie.title}</h3>
                <p>Genre: ${movie.genre}</p>
                <p>Rating: ${movie.rating}</p>
            `;
            recommendationsContainer.appendChild(movieElement);
        });
    }
});