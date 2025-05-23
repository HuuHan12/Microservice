# Stage 1: Use maven for building
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app


COPY ./bookservice ./bookservice
COPY ./commanservice ./commanservice

WORKDIR /app/commanservice
RUN mvn clean install -DskipTests

WORKDIR /app/bookservice
RUN mvn clean package -DskipTests

# Stage 2: use OpenJDK for running
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/bookservice/target/*.jar bookservice.jar
EXPOSE 9001
ENTRYPOINT ["java","-jar","bookservice.jar"]