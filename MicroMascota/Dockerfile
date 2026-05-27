#build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

#dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

#compilación
COPY src ./src
RUN mvn clean package -DskipTests

#run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

#jar
COPY --from=build /app/target/*.jar app.jar

#port
EXPOSE 8081

#ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]