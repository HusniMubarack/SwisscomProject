FROM openjdk:11-jre-slim
WORKDIR /app
COPY ./target/user-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "user-0.0.1-SNAPSHOT.jar"]
