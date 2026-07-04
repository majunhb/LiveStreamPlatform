FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-service-gift/ live-service-gift/
RUN mvn dependency:go-offline -pl live-service-gift -am
COPY . .
RUN mvn package -pl live-service-gift -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-service-gift/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
