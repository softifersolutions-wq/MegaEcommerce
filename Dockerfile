# ---------- Stage 1: build the jar with Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first so Docker can cache the downloaded dependencies
# (this layer only re-runs when pom.xml actually changes)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Now copy the actual source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: run the jar on a lean JRE ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the built jar from the build stage — final image doesn't need Maven at all
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]