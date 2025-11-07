FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY SimpleWebServer.java .
RUN javac SimpleWebServer.java
EXPOSE 8080
CMD ["java", "SimpleWebServer"]