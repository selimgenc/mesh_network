# Start with a base image containing Java runtime
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the jar file into the container
COPY build/libs/mesh-0.0.1-SNAPSHOT.jar mesh-0.0.1-SNAPSHOT.jar

# Run the jar file
ENTRYPOINT ["java","-jar","/app/mesh-0.0.1-SNAPSHOT.jar"]
