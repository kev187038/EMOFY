FROM openjdk:17
COPY target/emofy-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "emofy-0.0.1-SNAPSHOT.jar"]
