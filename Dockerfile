FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Jenkins a déjà construit le JAR via mvn clean package
COPY target/java-maven-app-*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD pgrep -f java || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
