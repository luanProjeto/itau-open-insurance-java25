# Build using Java 25; keep the runtime image independent of Maven.
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd -r -u 10001 appuser
COPY --from=build /workspace/target/itau-open-insurance-1.0.0.jar /app/app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
