FROM openjdk:17-alpine

COPY "build/libs/hs-user-*.jar" application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]
