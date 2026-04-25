# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Скопировать код
COPY src/TodoApp.java .

# Скомпилировать код
RUN javac TodoApp.java

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Скопировать драйвер PostgreSQL
ADD https://jdbc.postgresql.org/download/postgresql-42.7.3.jar postgresql.jar

# Скопировать приложение
COPY --from=builder /app/TodoApp.class .

# Запуск с подключением драйвера в classpath
ENTRYPOINT ["java", "-cp", ".:postgresql.jar", "TodoApp"]
