# Etapa 1: Compilação do backend
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

# Etapa 2: Imagem de execução
FROM eclipse-temurin:17-jre

WORKDIR /app

# Usuário sem privilégios de root
RUN groupadd --system bths \
    && useradd --system --gid bths --no-create-home bths

# Copiar somente o executável compilado
COPY --from=build --chown=bths:bths \
    /workspace/target/platform-0.0.1-SNAPSHOT.jar /app/app.jar

USER bths

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
