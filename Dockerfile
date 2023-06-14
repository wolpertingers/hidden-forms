FROM openjdk:17-jdk-slim
ENV HTTP_PORT=8080
COPY target/quarkus-app/quarkus-run.jar app.jar
EXPOSE $HTTP_PORT
ENTRYPOINT java -Dquarkus.http.port=$HTTP_PORT -jar /app.jar