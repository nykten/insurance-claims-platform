FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./pom.xml
COPY claims-service/pom.xml ./claims-service/pom.xml
COPY notification-service/pom.xml ./notification-service/pom.xml
COPY claims-service/src ./claims-service/src
RUN mvn -f pom.xml -pl claims-service -am package -DskipTests
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/claims-service/target/claims-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
