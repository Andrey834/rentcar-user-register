FROM amazoncorretto:21-alpine-full
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]