# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:17-al2-jdk

# Make port 8081 available to the world outside this container
EXPOSE 8082

# Specify the built JAR file path and add it to the container as member-api.jar
ARG JAR_FILE=build/libs/recipe-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} recipe-api.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/recipe-api.jar"]