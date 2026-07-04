FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-gateway/ live-gateway/
RUN mvn dependency:go-offline -pl live-gateway -am
COPY . .
RUN mvn package -pl live-gateway -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
