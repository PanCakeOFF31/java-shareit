FROM amazoncorretto:11-alpine-jdk
COPY target/"shareit-server-0.0.1-SNAPSHOT.jar" shareit-server-app.jar
ENTRYPOINT ["java", "-jar", "/shareit-server-app.jar"]