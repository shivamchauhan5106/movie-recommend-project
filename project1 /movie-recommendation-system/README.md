# Movie Recommendation System

This project is a full-stack movie recommendation system that provides users with personalized movie suggestions based on their preferences. The application is built using a Java backend with Spring Boot and a frontend developed with HTML, CSS, and JavaScript.

## Project Structure

```
movie-recommendation-system
├── frontend
│   ├── html
│   │   └── index.html
│   ├── css
│   │   └── styles.css
│   └── js
│       ├── app.js
│       ├── api.js
│       └── utils.js
├── backend
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── movieapp
│   │   │   │           ├── App.java
│   │   │   │           ├── controllers
│   │   │   │           │   └── MovieController.java
│   │   │   │           ├── services
│   │   │   │           │   └── RecommendationService.java
│   │   │   │           └── models
│   │   │   │               └── Movie.java
│   │   │   └── resources
│   │   │       └── application.properties
│   │   └── test
│   │       └── java
│   └── pom.xml
├── config
│   └── database.sql
└── README.md
```

## Setup Instructions

### Prerequisites

- Java 11 or higher
- Maven
- Node.js and npm (for frontend development)

### Backend Setup

1. Navigate to the `backend` directory.
2. Run the following command to build the project:
   ```
   mvn clean install
   ```
3. Configure your database connection in `src/main/resources/application.properties`.
4. Run the application:
   ```
   mvn spring-boot:run
   ```

### Frontend Setup

1. Navigate to the `frontend` directory.
2. Open `frontend/html/index.html` in your web browser to view the application.

## Usage

- The application allows users to view movie recommendations based on their preferences.
- Users can interact with the frontend to receive personalized suggestions.

## Contributing

Feel free to fork the repository and submit pull requests for any improvements or features you would like to add.

## License

This project is licensed under the MIT License.