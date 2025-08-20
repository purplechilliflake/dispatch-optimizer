# Dispatch Optimizer

A RESTful service built with Spring Boot that optimizes the dispatch of delivery orders to a fleet of vehicles. The service assigns orders based on priority, vehicle capacity, and travel distance to generate an efficient delivery plan.

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA**
- **Maven**
- **H2 In-Memory Database**

## Quickstart

### Prerequisites

- JDK 17 or higher installed
- Apache Maven

### Running Locally

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/purplechilliflake/dispatch-optimizer.git
    cd dispatch-optimizer
    ```

2.  **Navigate into the project directory:**
    ```bash
    cd demo
    ```

3.  **Build the project:**
    (This command also compiles, runs tests, and packages the application)
    ```bash
    ./mvnw clean install
    ```

4.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The API is now running and available at `http://localhost:8080`.

## Testing with Postman

The API can be easily tested using the shared Postman collection, which includes all required endpoints and the full sample dataset.

### Option 1: Run in Postman (Recommended)

Click the button below to fork the collection directly into your own Postman workspace.

[![Run in Postman](https://run.pstmn.io/button.svg)](https://rupallahre-8693365.postman.co/workspace/Rupal-Lahre's-Workspace~d35841ac-7f83-4249-8896-2ddd433a6860/collection/44138209-e5452681-8fdb-4a96-a6fa-50bb42384dcf?action=share&source=copy-link&creator=44138209)

### Option 2: Manual Import

1.  Open Postman.
2.  Go to `File` > `Import...`.
3.  Select the `Link` tab.
4.  Paste the following URL:

    `https://rupallahre-8693365.postman.co/workspace/Rupal-Lahre's-Workspace~d35841ac-7f83-4249-8896-2ddd433a6860/collection/44138209-e5452681-8fdb-4a96-a6fa-50bb42384dcf?action=share&source=copy-link&creator=44138209`

### Execution Order

Once imported, make sure the application is running locally and execute the requests in the numbered order to see the optimizer in action:
1.  `1. Add Orders`
2.  `2. Add Vehicles`
3.  `3. Get Dispatch Plan`

> **Note:** The entire sequence must be run in the same session, as the in-memory database is cleared every time the application restarts.

---

## Running with Docker

This application is containerized and can be run using Docker, without needing to install Java or Maven locally.

### Prerequisites

- Docker Desktop installed and running.

### Steps

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/purplechilliflake/dispatch-optimizer.git
    cd dispatch-optimizer
    ```

2.  **Build the Docker image:**
    From the root directory, run the build command:
    ```bash
    docker build -t dispatch-optimizer .
    ```

3.  **Run the container:**
    ```bash
    docker run -p 8080:8080 dispatch-optimizer
    ```
    The application will now be running and accessible at `http://localhost:8080`. You can test the endpoints using the Postman collection.