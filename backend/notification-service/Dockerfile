FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY claims-service/pom.xml ./claims-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY notification-service/src ./notification-service/src
RUN mvn -f pom.xml -pl notification-service -am package -DskipTests
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/notification-service/target/notification-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
