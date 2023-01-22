FROM maven:3.8.6-jdk-11-slim as builder
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
ENV TZ="Europe/Moscow"
RUN mvn -f /usr/src/app/pom.xml -Dmaven.test.skip package spring-boot:repackage

FROM adoptopenjdk/openjdk11:jre-11.0.11_9
COPY --from=builder /usr/src/app/target/javaproTeams30TelegramBot-0.0.1-SNAPSHOT.jar telegram-bot.jar
CMD java -jar telegram-bot.jar
