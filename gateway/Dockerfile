FROM amazoncorretto:11-alpine-jdk
#RUN apk add --no-cache tzdata
#ENV TZ=Europe/Moscow
COPY target/*.jar gateway.jar
ENTRYPOINT ["java","-jar","/gateway.jar"]