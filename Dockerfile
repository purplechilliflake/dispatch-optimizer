# --- Stage 1: Build the application ---
# We use a specific version of the official Maven image which includes JDK 17.
# This stage is only for building the .jar file.
FROM maven:3.9.6-eclipse-temurin-17-focal AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file first. This layer is cached by Docker.
# If pom.xml doesn't change, Docker won't re-download dependencies.
COPY ./demo/pom.xml .

# Copy the rest of the project source code
COPY ./demo/src ./src

# Run the Maven package command to build the application .jar file.
# We skip tests here because we assume they've been run in the CI pipeline.
RUN mvn -f pom.xml clean package -DskipTests


# --- Stage 2: Create the final, lightweight image ---
# We use a slim OpenJDK 17 image that is optimized for running Java apps.
# It doesn't contain Maven or any build tools, making it smaller and more secure.
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the executable .jar file that was created in the 'build' stage.
# We rename it to app.jar for a simpler run command.
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080. This tells Docker that the application inside the container
# will be listening for traffic on this port.
EXPOSE 8080

# The command to run when the container starts.
# This executes our Spring Boot application.
ENTRYPOINT ["java", "-jar", "app.jar"]