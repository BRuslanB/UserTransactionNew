# Base layer with dependencies
FROM openjdk:21 as base
WORKDIR /app
COPY ../gradle ./gradle/
COPY ../build.gradle settings.gradle ./
COPY ../gradlew gradlew.bat ./
#RUN ./gradlew dependencies

# Layer with application code
FROM base as code
COPY ../src ./src/
#RUN ./gradlew build

# Layer with application resources and generated protobuf files
FROM code as resources
COPY ../src/main/resources/ ./resources/
COPY ../build/libs/*.jar /app/build/libs/
COPY ../build/generated/source/proto/main/java/ /app/generated/proto/

# Final layer with runtime environment
FROM openjdk:21
WORKDIR /app
COPY --from=resources /app/build/libs/*.jar app.jar

# Delay befory application launch
ENTRYPOINT ["sh", "-c", "sleep 120 && java -jar app.jar"]