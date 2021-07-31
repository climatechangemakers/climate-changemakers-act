#!/bin/bash

pg_dump --no-acl --no-owner -s postgres -U ccm_readonly -h ccm-db.c51ekbqkhdej.us-west-2.rds.amazonaws.com -p 5432 > ccm_schema.sql
