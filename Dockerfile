# Stage 1: Build stage (optionnel car Maven build sera fait par Jenkins)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copier le JAR depuis le stage de build (ou depuis target/ si build local)
COPY --from=build /app/target/java-maven-app-*.jar app.jar

# Changer les permissions
RUN chown -R appuser:appgroup /app

# Utiliser l'utilisateur non-root
USER appuser

# Exposer le port (même si cette app n'est pas un serveur web)
EXPOSE 8080

# Healthcheck (optionnel)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD pgrep -f java || exit 1

# Commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
