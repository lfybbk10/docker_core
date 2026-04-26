FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY src ./src

RUN javac -d out src/main/java/ru/mentee/power/CalculatorV1.java

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/out/ /app/

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "/app", "ru.mentee.power.CalculatorV1"]
