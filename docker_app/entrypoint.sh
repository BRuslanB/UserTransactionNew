#!/bin/bash

# Wait Cassandra and Postgres
./wait-for-databases_ready.sh

# Launch tests
# ./gradlew clean
# ./gradlew test -D spring.profiles.active=test

# Change permissions back to original (e.g., 755)
# chmod -R 755 /app > /dev/null 2>&1
# chmod -R 755 /.gradle > /dev/null 2>&1

# Launch application
java -jar app.jar