FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY src ./src

RUN javac -d out \
    src/main/java/ru/mentee/power/DatabaseConfig.java \
    src/main/java/ru/mentee/power/ExternalApiClient.java \
    src/main/java/ru/mentee/power/ConfigurableCalculator.java


FROM eclipse-temurin:21-jre
WORKDIR /app

ADD https://jdbc.postgresql.org/download/postgresql-42.7.3.jar postgresql.jar

COPY --from=builder /app/out/ /app/

EXPOSE 8080

ENTRYPOINT ["java", "-cp", ".:postgresql.jar", "ru.mentee.power.ConfigurableCalculator"]