# Ещё большая оптимизация размера путём сборки минимальной JRE

FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY src/WebApp.java .
RUN javac WebApp.java

RUN jlink \
    --add-modules java.base,jdk.httpserver \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --compress=2 \
    --output /opt/jre-min

FROM debian:bookworm-slim
WORKDIR /app
COPY --from=builder /opt/jre-min /opt/jre-min
COPY --from=builder /app/WebApp.class .
ENV PATH="/opt/jre-min/bin:$PATH"
EXPOSE 8080
ENTRYPOINT ["java", "WebApp"]
