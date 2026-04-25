FROM maven:3.9.8-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
ARG SERVICE
ENV SERVICE=${SERVICE}
WORKDIR /app

COPY --from=builder /app/target/classes ./classes
COPY --from=builder /app/target/dependency ./dependency

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -cp '/app/classes:/app/dependency/*' ru.mentee.power.${SERVICE}"]
