FROM amazoncorretto:17.0.7-alpine
WORKDIR /app
COPY target/api-gateway.jar /app/api-gateway.jar
EXPOSE 8080
CMD ["java", "-jar", "api-gateway.jar"]