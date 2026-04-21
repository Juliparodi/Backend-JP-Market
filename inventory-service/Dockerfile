FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN useradd -m appuser

COPY --chown=appuser:appuser target/inventory-service-*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseContainerSupport","-jar","app.jar"]