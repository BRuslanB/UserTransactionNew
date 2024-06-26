# Base layer with dependencies
FROM openjdk:21-slim as base
WORKDIR /app

# Add user and group with specific UID and GID
#RUN groupadd -g 1001 myusergroup && \
#    useradd -u 1001 -g myusergroup myuser

# Copy gradle files
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./
COPY gradlew ./
COPY gradlew.bat ./

# Fix line endings for gradlew
RUN sed -i 's/\r$//' gradlew

# Install dependencies
RUN chmod +x ./gradlew && \
    ./gradlew dependencies

# Layer with application code
FROM base as code
WORKDIR /app
COPY src ./src/

# Build app excluding tests (-x test)
RUN ./gradlew build -x test && \
    ls -la build/libs && \
    ls -la build/generated/source/proto/main/java/

# Final layer with runtime environment
FROM openjdk:21-slim
WORKDIR /app

# Add user and group with specific UID and GID
#RUN groupadd -g 1001 myusergroup && \
#    useradd -u 1001 -g myusergroup myuser

# Copy base layer
COPY --from=base /app/gradle ./gradle/
COPY --from=base /app/build.gradle ./
COPY --from=base /app/settings.gradle ./
COPY --from=base /app/gradlew ./
COPY --from=base /app/gradlew.bat ./

# Copy code layer
COPY --from=code /app/build/libs/*.jar app.jar
COPY --from=code /app/build/generated/source/proto/main/java/ /app/generated/proto/
COPY --from=code /app/src /app/src

# Copy scripts
COPY docker_app/wait-for-databases_ready.sh ./
COPY docker_app/entrypoint.sh ./

# Fix line endings for scripts
RUN sed -i 's/\r$//' wait-for-databases_ready.sh
RUN sed -i 's/\r$//' entrypoint.sh

# Install netcat-openbsd for waiting script
RUN apt-get update && \
    apt-get install -y netcat-openbsd

# Ensure scripts are executable
RUN chmod +x ./wait-for-databases_ready.sh && \
    chmod +x ./entrypoint.sh

# Ensure gradlew is executable
RUN chmod +x ./gradlew

# Create necessary directories and set permissions
#RUN mkdir -p /.gradle /home/myuser /app/build/tmp && \
#    chown -R myuser:myusergroup /.gradle /home/myuser /app && \
#    chmod -R 777 /app && \
#    chmod -R 777 /.gradle /home/myuser

RUN mkdir -p /.gradle /home/myuser /app/build/tmp && \
    chown -R 777 /.gradle /home/myuser /app

# Ensure all subdirectories have correct permissions
# RUN find /app -type d -exec chmod 777 {} \; && \
#    find /app -type f -exec chmod 666 {} \;

# Switch to the new user
#USER myuser

# Set the entrypoint to execute the script
ENTRYPOINT ["./entrypoint.sh"]