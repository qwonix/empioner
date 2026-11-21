FROM gradle:8.14.3-jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src/telegram-bot
RUN gradle :telegram-bot:telegram-bot-microservice:bootJar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S -g 10001 empioner && adduser -S -D -H -u 10001 -G empioner empioner

COPY --from=build /home/gradle/src/telegram-bot/telegram-bot-microservice/build/libs/*.jar /app/app.jar
USER 10001:10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
