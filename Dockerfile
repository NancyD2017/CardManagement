FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon
RUN ls -l /app/build/libs/
RUN ls -l /app/src/main/resources/

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/CreditCardManagement-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]