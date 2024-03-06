FROM openjdk:17
COPY target/spring-scala-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "spring-scala-0.0.1-SNAPSHOT.jar"]
