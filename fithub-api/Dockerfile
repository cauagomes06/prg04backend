# Etapa 1: Construção (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Faz o build saltando os testes para ser mais rápido
RUN mvn clean package -DskipTests

# Etapa 2: Execução (Run)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copia o jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Cria a pasta de uploads para as imagens
RUN mkdir uploads

# Expõe a porta 8080
EXPOSE 8080

# Comando para iniciar
ENTRYPOINT ["java", "-jar", "app.jar"]