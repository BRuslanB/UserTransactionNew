# Base layer with dependencies
FROM postgres:16-alpine

# Copy the script and check it
COPY ./docker_postgres/init_test_db.sh /docker-entrypoint-initdb.d/init_test_db.sh

# Remove CR characters
RUN sed -i 's/\r$//' /docker-entrypoint-initdb.d/init_test_db.sh

# Set execution rights for the script
RUN chmod +x /docker-entrypoint-initdb.d/init_test_db.sh