FROM amazoncorretto:11-alpine-jdk
#RUN apk add --no-cache tzdata
#ENV TZ=Europe/Moscow
COPY target/*.jar server.jar
ENTRYPOINT ["java","-jar","/server.jar"]