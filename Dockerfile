# STAGE 1: The Builder
# We bring in a heavy image that includes Maven to compile your code.
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# We package the app into a .jar file, skipping tests to make it fast
RUN mvn clean package -DskipTests

# STAGE 2: The Runner
# We throw away the heavy Maven stuff and only keep a lightweight Java runtime.
# This makes your final image tiny, secure, and fast to boot up.
FROM eclipse-temurin:21-jre
WORKDIR /app
# We copy the finished .jar file from Stage 1
COPY --from=build /app/target/*.jar app.jar
# Expose the port (Render/GCP will override this, but it's good practice)
EXPOSE 8080
# The command that actually starts the server
ENTRYPOINT ["java", "-jar", "app.jar"]