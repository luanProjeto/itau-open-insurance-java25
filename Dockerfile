
# Build usando Java 25 e Maven
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn -B -ntp clean package -DskipTests

# Imagem de execução com Java 25
FROM eclipse-temurin:25-jre

WORKDIR /app

# Criar usuário sem privilégios
RUN useradd -r -u 10001 appuser

# Criar diretório do banco H2 e conceder permissão
RUN mkdir -p /app/data && chown -R appuser:appuser /app/data

# Copiar aplicação compilada
COPY --from=build /workspace/target/itau-open-insurance-1.0.0.jar /app/app.jar

# Executar com usuário sem privilégios
USER 10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
