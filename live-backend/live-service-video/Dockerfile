FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-service-video/ live-service-video/
RUN mvn dependency:go-offline -pl live-service-video -am
COPY . .
RUN mvn package -pl live-service-video -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-service-video/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
