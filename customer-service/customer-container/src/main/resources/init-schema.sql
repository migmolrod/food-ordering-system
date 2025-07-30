-- ################################################################
-- SCHEMA
-- ################################################################
DROP SCHEMA IF EXISTS customer CASCADE;
CREATE SCHEMA customer;


-- ################################################################
-- EXTENSIONS
-- ################################################################

-- uuid-ossp
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- ################################################################
-- TABLES
-- ################################################################

-- customers
DROP TABLE IF EXISTS customer.customers CASCADE;
CREATE TABLE customer.customers
(
    id         uuid                                           NOT NULL,
    username   CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    first_name CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    last_name  CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pk PRIMARY KEY (id)
);
