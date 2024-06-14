#!/bin/sh

# Wait for Cassandra to become available on port 9042
until nc -z -v -w30 db_cassandra 9042
do
  echo "Waiting for cassandra to be ready..."
  sleep 10
done

echo "Cassandra is up"

# Wait for PostgreSQL to become available on port 5432
until nc -z -v -w30 db_postgres 5432
do
  echo "Waiting for postgres to be ready..."
  sleep 10
done

echo "Postgres is up"

echo "All dependencies are up - executing command"
exec "$@"