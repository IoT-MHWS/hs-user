FROM openjdk:17-alpine

RUN mkdir -p /app/services

WORKDIR /app

COPY "build/libs/hs-users-*-SNAPSHOT.jar" ./application.jar

COPY "services/" ./services/

ENTRYPOINT ["java", "-jar", "application.jar"]
