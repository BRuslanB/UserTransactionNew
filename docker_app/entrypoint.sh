#!/bin/bash

# Wait Cassandra and Postgres
./wait-for-cassandra.sh

# Wait additional 30 sec
#sleep 30

# Launch tests
./gradlew test

# Change permissions back to original (e.g., 755)
chmod -R 755 /app > /dev/null 2>&1
chmod -R 755 /.gradle > /dev/null 2>&1

# Launch application
java -jar app.jar