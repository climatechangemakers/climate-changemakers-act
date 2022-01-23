#!/bin/bash

# Allocate a new temporary directory for the migration files. 
tmp_dir=$(mktemp -d -t migrations-XXXXXX)

# Move the SQLDelight migrations to the temporary directory. 
cp backend/src/main/sqldelight/migrations/*.sqm $tmp_dir 

# Rename the .sqm files in tmp_dir to .sql files. This assumes that are valid SQL. 
find $tmp_dir -name "*.sqm" -exec sh -c 'mv "$1" "${1%.sqm}.sql"' _ {} \;

ls -la $tmp_dir

# Build the jdbc URI.
POSTGRES_URL="jdbc:postgresql://${POSTGRES_HOSTNAME}:${POSTGRES_PORT}/${POSTGRES_DB}"

# Initiate the migration. 
flyway -user=${POSTGRES_USER} \
       -X \
       -password=${POSTGRES_PASSWORD} \
       -table=schema_history \
       -locations="filesystem:$tmp_dir" \
       -url=${POSTGRES_URL} \
       migrate