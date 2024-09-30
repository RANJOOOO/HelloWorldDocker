# Use the official Gradle image to build the Java project
FROM gradle:jdk21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only the Gradle build files first to take advantage of Docker's layer caching
COPY build.gradle settings.gradle /app/

# Download the Gradle dependencies to cache them
RUN gradle build --no-daemon --parallel --continue || true

# Now copy the rest of the application source code
COPY src /app/src

# Build the Java project (Gradle will cache the dependencies from the previous step)
RUN gradle shadowJar --no-daemon

# Use a smaller base image to run the built application (reducing final image size)
FROM openjdk:21-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built fat jar file from the build stage
COPY --from=build /app/build/libs/*-all.jar /app/app.jar

# Run the integration tests
CMD ["java", "-jar", "/app/app.jar", "test"]