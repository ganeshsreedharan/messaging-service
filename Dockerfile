FROM openjdk:17-alpine
MAINTAINER experto.com
VOLUME /tmp
EXPOSE 8080
ADD build/libs/messaging-service-0.0.1-SNAPSHOT.jar messaging-service.jar
ENTRYPOINT ["java","-jar","/messaging-service.jar"]