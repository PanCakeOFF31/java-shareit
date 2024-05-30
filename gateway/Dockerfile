FROM amazoncorretto:11-alpine-jdk
COPY target/"shareit-gateway-0.0.1-SNAPSHOT.jar" shareit-gateway-app.jar
ENTRYPOINT ["java", "-jar", "/shareit-gateway-app.jar"]
