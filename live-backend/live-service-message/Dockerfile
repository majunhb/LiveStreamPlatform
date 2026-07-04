FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-service-message/ live-service-message/
RUN mvn dependency:go-offline -pl live-service-message -am
COPY . .
RUN mvn package -pl live-service-message -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-service-message/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
