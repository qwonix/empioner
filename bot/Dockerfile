FROM gradle:8.4-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG JAR_NAME=movie-player-bot-tg
COPY --from=build /home/gradle/src/build/libs/${JAR_NAME}-*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]