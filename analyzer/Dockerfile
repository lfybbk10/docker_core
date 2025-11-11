FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY TextAnalyzer.java .
RUN javac TextAnalyzer.java
ENTRYPOINT ["java", "TextAnalyzer"]