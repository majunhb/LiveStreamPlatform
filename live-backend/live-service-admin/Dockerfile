FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY live-common/ live-common/
COPY live-service-admin/ live-service-admin/
RUN mvn dependency:go-offline -pl live-service-admin -am
COPY . .
RUN mvn package -pl live-service-admin -am -DskipTests

FROM eclipse-temurin:17-jre
COPY --from=build /app/live-service-admin/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
