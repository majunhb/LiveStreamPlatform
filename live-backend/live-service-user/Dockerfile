FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-service-user/ live-service-user/
RUN mvn dependency:go-offline -pl live-service-user -am
COPY . .
RUN mvn package -pl live-service-user -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-service-user/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
