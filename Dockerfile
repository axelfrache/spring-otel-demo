FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -q -ntp -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -ntp -DskipTests package

FROM eclipse-temurin:24-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]