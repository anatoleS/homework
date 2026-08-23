FROM eclipse-temurin:25-jre

WORKDIR /app
COPY build/libs/homework-1.0-SNAPSHOT-fat.jar app.jar
COPY hw08-gc/tools/sweep.sh sweep.sh
RUN chmod +x /app/sweep.sh && mkdir -p /results

ENV RESULTS=/results JAR=/app/app.jar

ENTRYPOINT ["/app/sweep.sh"]
