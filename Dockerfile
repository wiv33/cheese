FROM openjdk:17-alpine

MAINTAINER PS

ADD build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]