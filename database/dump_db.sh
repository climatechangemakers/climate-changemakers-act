#!/bin/bash

pg_dump --no-acl\
       	--no-owner\
       	--exclude-table-data "*action*"\
       	--exclude-table-data "*attendanc*"\
       	--exclude-table-data "hoa_*"\
       	--exclude-table-data "segments"\
       	--exclude-table-data "contacts"\
       	postgres\
       	-U ccm_readonly\
       	-h ccm-db.c51ekbqkhdej.us-west-2.rds.amazonaws.com\
       	-p 5432\
       	> ccm_schema.sql
