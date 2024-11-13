FROM openjdk:17-jdk-slim
COPY /starter_code/target/auth-course-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]