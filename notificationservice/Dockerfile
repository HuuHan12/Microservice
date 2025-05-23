# Stage 1: Use maven for building
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

COPY ./notificationservice ./notificationservice
COPY ./commanservice ./commanservice

WORKDIR /app/commanservice
RUN mvn clean install -DskipTests

WORKDIR /app/notificationservice
RUN mvn clean package -DskipTests

# Stage 2: use OpenJDK for running
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/notificationservice/target/*.jar notificationservice.jar
EXPOSE 9003
ENTRYPOINT ["java","-jar","notificationservice.jar"]