# Base layer with dependencies
FROM openjdk:21-slim as base
WORKDIR /app
COPY gradle ./gradle/
COPY build.gradle settings.gradle ./
COPY gradlew gradlew.bat ./
RUN chmod +x gradlew && ./gradlew dependencies

# Layer with application code
FROM base as code
WORKDIR /app
COPY src ./src/
# build app excluded test (-x test)
RUN ./gradlew build -x test && ls -la build/libs && ls -la build/generated/source/proto/main/java/

# Final layer with runtime environment
FROM openjdk:21-slim
WORKDIR /app
COPY --from=code /app/build/libs/*.jar app.jar
COPY --from=code /app/build/generated/source/proto/main/java/ /app/generated/proto/
COPY --from=code /app/src /app/src
COPY gradle ./gradle/
COPY build.gradle settings.gradle ./
COPY gradlew gradlew.bat ./
COPY docker_app/wait-for-cassandra.sh ./
COPY docker_app/entrypoint.sh ./

# Install netcat-openbsd for waiting script
RUN apt-get update && apt-get install -y netcat-openbsd

# Ensure the script is executable
#RUN chmod +x ./wait-for-cassandra.sh && chmod +x ./gradlew

# Ensure the scripts are executable
RUN chmod +x ./wait-for-cassandra.sh && chmod +x ./gradlew && chmod +x ./entrypoint.sh

# Create directory and change permissions access (e.g., 777)
RUN chmod -R 777 /app
RUN mkdir -p /.gradle && chmod -R 777 /.gradle

# Создайте том для .gradle внутри папки /app
#VOLUME /app/.gradle

# Delay before application launch
#ENTRYPOINT ["./wait-for-cassandra.sh", "java", "-jar", "app.jar"]

# Set the entrypoint to execute tests
#ENTRYPOINT ["bash", "-c", "./gradlew test && ./wait-for-cassandra.sh java -jar app.jar"]

# Set the entrypoint to execute the script
ENTRYPOINT ["./entrypoint.sh"]