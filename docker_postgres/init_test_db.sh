#!/bin/bash

## This script will create a docker-entrypoint.sh file that:
## - executes any *.sh script found in /docker-entrypoint-initdb.d

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE transaction_new_db_test;
EOSQL