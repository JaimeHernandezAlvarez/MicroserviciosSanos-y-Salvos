# Etapa de compilación
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Instalar maven si el alpine no lo trae (o puedes usar la distro standard de debian si prefieres)
RUN apk add --no-cache maven

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

#port
EXPOSE 8080

#ejecutar
ENTRYPOINT ["java", "-jar", "app.jar"]