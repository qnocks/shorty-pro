FROM gradle:8.14.2-jdk21 AS build

WORKDIR /home/gradle/workspace

COPY shorty-pro-shared ./shorty-pro-shared
COPY url-shortener-service ./url-shortener-service

WORKDIR /home/gradle/workspace/url-shortener-service
RUN gradle --no-daemon clean build -x test

FROM eclipse-temurin:21

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /home/gradle/workspace/url-shortener-service/build/libs/*.jar app.jar

EXPOSE 8082
CMD ["java", "-jar", "app.jar"]
