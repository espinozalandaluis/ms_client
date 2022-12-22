FROM openjdk:11
VOLUME /tmp
EXPOSE 8082
ADD "./target/client-0.0.1-SNAPSHOT.jar" "ms-client.jar"
ENTRYPOINT ["java","-jar","ms-client.jar"]