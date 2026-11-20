-- Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;
-- For key-value pairs e.g.: '"city"=>"Delhi", "country"=>"India"'
CREATE EXTENSION IF NOT EXISTS hstore;
-- For UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";