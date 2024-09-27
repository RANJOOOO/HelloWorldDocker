# Use the official Gradle image to build the Java project
FROM gradle:jdk21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only the Gradle build files
COPY build.gradle settings.gradle /app/

# Now copy the rest of the application source code
COPY src /app/src
COPY users.db /app/users.db

# Run the Java application
CMD ["gradle", "run"]
