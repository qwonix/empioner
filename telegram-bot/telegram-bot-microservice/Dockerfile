FROM gradle:8.4-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/telegram-bot
RUN gradle :telegram-bot:telegram-bot-microservice:bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /home/gradle/src/telegram-bot/telegram-bot-microservice/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]