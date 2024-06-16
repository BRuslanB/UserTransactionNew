# Base layer with dependencies
FROM openjdk:21-slim as base
WORKDIR /app
# Copy gradle files
COPY gradle ./gradle/
COPY build.gradle ./
COPY settings.gradle ./
COPY gradlew ./
COPY gradlew.bat ./

# Check the ending line (LF) in the gradlew file
RUN sed -i 's/\r$//' gradlew

# Execute the command to install dependencies
RUN chmod +x ./gradlew && ./gradlew dependencies

# Layer with application code
FROM base as code
WORKDIR /app
COPY src ./src/

# Create the build directory and set permissions
RUN mkdir -p /app/build && chmod -R 777 /app/build

# Build app excluding tests (-x test)
RUN ./gradlew build -x test && ls -la build/libs && ls -la build/generated/source/proto/main/java/

# Final layer with runtime environment
FROM openjdk:21-slim
WORKDIR /app
# Copy Base layer
COPY --from=base /app/gradle ./gradle/
COPY --from=base /app/build.gradle ./
COPY --from=base /app/settings.gradle ./
COPY --from=base /app/gradlew ./
COPY --from=base /app/gradlew.bat ./
# Copy Code layer
COPY --from=code /app/build/libs/*.jar app.jar
COPY --from=code /app/build/generated/source/proto/main/java/ /app/generated/proto/
COPY --from=code /app/src /app/src
# Copy scripts
COPY docker_app/wait-for-databases_ready.sh ./
COPY docker_app/entrypoint.sh ./

# Check the ending line (LF) in the wait-for-databases_ready.sh file
RUN sed -i 's/\r$//' wait-for-databases_ready.sh

# Check the ending line (LF) in the entrypoint.sh file
RUN sed -i 's/\r$//' entrypoint.sh

# Install netcat-openbsd for waiting script
RUN apt-get update && apt-get install -y netcat-openbsd

# Ensure the scripts are executable
RUN chmod +x ./wait-for-databases_ready.sh && chmod +x ./gradlew && chmod +x ./entrypoint.sh

# Create directory and change permissions access (e.g., 777)
RUN chmod -R 777 /app
RUN mkdir -p /.gradle && chmod -R 777 /.gradle

# Set the entrypoint to execute the script
ENTRYPOINT ["./entrypoint.sh"]