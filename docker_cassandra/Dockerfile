# Base layer with dependencies
FROM cassandra:4.1.4

# Fix UTF-8 accents in init scripts
ENV LANG C.UTF-8

# Copy init_script.cql to the container initialization directory
COPY ../src/main/resources/cassandra/init_script.cql /docker-entrypoint-initdb.d/init_script.cql

# This is the script that will patch the already existing entrypoint from cassandra image
COPY ../docker_cassandra/entrypoint.sh /

# Check the ending line (LF) in the init_script.cql file
RUN sed -i 's/\r$//' /docker-entrypoint-initdb.d/init_script.cql

# Check the ending line (LF) in the entrypoint.sh file
RUN sed -i 's/\r$//' entrypoint.sh

# Adding a chmod command after the copy command
RUN ["chmod", "+x", "/entrypoint.sh"]

# Exposing ports
EXPOSE 7000 7001 7199 9042 9160

# Override ENTRYPOINT, keep CMD
ENTRYPOINT ["/entrypoint.sh"]

# Keep CMD
CMD ["cassandra", "-f", "-R"]