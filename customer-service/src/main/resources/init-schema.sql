-- ################################################################
-- SCHEMA
-- ################################################################
DROP SCHEMA IF EXISTS customer CASCADE;

CREATE SCHEMA customer;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ################################################################
-- CUSTOMERS TABLE
-- ################################################################
DROP TABLE IF EXISTS customer.customers CASCADE;

CREATE TABLE customer.customers
(
    id         uuid                                           NOT NULL,
    username   character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name  character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pk PRIMARY KEY (id)
);

-- ################################################################
-- MATERIALIZED VIEW
-- ################################################################
DROP MATERIALIZED VIEW IF EXISTS customer.order_customer_m_view;

CREATE MATERIALIZED VIEW customer.order_customer_m_view TABLESPACE pg_default AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
WITH DATA;

REFRESH MATERIALIZED VIEW customer.order_customer_m_view;

-- FUNCTION TO REFRESH MATERIALIZED VIEW
DROP FUNCTION IF EXISTS customer.refresh_order_customer_m_view();

CREATE OR REPLACE FUNCTION customer.refresh_order_customer_m_view()
    RETURNS trigger
AS
'
    BEGIN
        REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
        RETURN NULL;
    end;
' LANGUAGE plpgsql;

-- TRIGGER TO REFRESH MATERIALIZED VIEW
DROP TRIGGER IF EXISTS customer_refresh_order_customer_m_view ON customer.customers;

CREATE TRIGGER customer_refresh_order_customer_m_view
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON customer.customers
    FOR EACH STATEMENT
EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
