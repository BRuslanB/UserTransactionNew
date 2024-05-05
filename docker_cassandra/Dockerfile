# Base layer with dependencies
FROM cassandra:4.1.4

# Fix UTF-8 accents in init scripts
ENV LANG C.UTF-8

# Copy init_script.cql to the container initialization directory
COPY ../src/main/resources/cassandra/init_script.cql /docker-entrypoint-initdb.d/init_script.cql

# This is the script that will patch the already existing entrypoint from cassandra image
COPY ../src/main/resources/cassandra/entrypoint.sh /

# Create cassandra user
#RUN useradd -m cassandra

# Change owner of initialization script and entrypoint script to cassandra
#RUN chown cassandra:cassandra /docker-entrypoint-initdb.d/init_script.cql /entrypoint.sh

# Adding a chmod command after the copy command
RUN ["chmod", "+x", "/entrypoint.sh"]
#RUN ["chmod", "+x", "/run-init-scripts.sh"]
#RUN ["chmod", "+x", "/patched-entrypoint.sh"]

# Устанавливаем права на запись для файла patched-entrypoint.sh
#RUN chown cassandra:cassandra /patched-entrypoint.sh && \
#    chmod u+x /patched-entrypoint.sh

# Override ENTRYPOINT, keep CMD
ENTRYPOINT ["/entrypoint.sh"]
CMD ["cassandra", "-f", "-R"]
#CMD ["cassandra", "-f"]