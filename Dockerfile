FROM gradle:jdk21

USER root
WORKDIR /app

COPY src src
COPY gradle gradle
COPY build.gradle build.gradle
COPY settings.gradle settings.gradle
COPY gradlew.bat gradlew.bat
COPY gradlew gradlew
RUN chown -R gradle:gradle /app
RUN ./gradlew clean build

ENTRYPOINT ["java", "-jar", "build/libs/HelloWorld1-8.2.jar"]