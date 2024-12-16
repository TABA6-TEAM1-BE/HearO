FROM openjdk:17
ARG JAR_FILE=/build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/firebase/taba-firebase-admin-sdk.json /taba-firebase-admin-sdk.json
ENTRYPOINT ["java","-jar","/app.jar"]