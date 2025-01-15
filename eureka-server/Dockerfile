#### Optimized ####
# Build stage
FROM openjdk:21-jdk-slim-bullseye AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y dos2unix && \
    dos2unix ./mvnw && \
    chmod +x ./mvnw && \
    ./mvnw package -DskipTests && \
    rm -rf /var/lib/apt/lists/*

# Runtime stage
FROM openjdk:21-jdk-slim-bullseye
WORKDIR /app
COPY --from=build /app/target/eureka-server-3.1.2.jar .
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "eureka-server-3.1.2.jar"]