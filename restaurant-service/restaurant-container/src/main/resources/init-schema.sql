-- ################################################################
-- SCHEMA
-- ################################################################
DROP SCHEMA IF EXISTS restaurant CASCADE;

CREATE SCHEMA restaurant;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ################################################################
-- RESTAURANTS TABLE
-- ################################################################
DROP TABLE IF EXISTS restaurant.restaurants;

CREATE TABLE restaurant.restaurants
(
    id     UUID                                           NOT NULL,
    name   CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    active BOOLEAN                                        NOT NULL,
    CONSTRAINT pk_restaurant PRIMARY KEY (id)
);

-- ################################################################
-- PRODUCTS TABLE
-- ################################################################
DROP TABLE IF EXISTS restaurant.products;

CREATE TABLE restaurant.products
(
    id        UUID                                           NOT NULL,
    name      CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    price     NUMERIC(10, 2)                                 NOT NULL,
    available BOOLEAN                                        NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

-- ################################################################
-- RESTAURANT PRODUCTS TABLE (RELATION)
-- ################################################################
DROP TABLE IF EXISTS restaurant.restaurant_products;

CREATE TABLE restaurant.restaurant_products
(
    id            UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    product_id    UUID NOT NULL,
    CONSTRAINT pk_restaurant_products PRIMARY KEY (id)
);

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT fk_restaurant_products_on_restaurant FOREIGN KEY (restaurant_id)
        REFERENCES restaurant.restaurants (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
        NOT VALID;

ALTER TABLE restaurant.restaurant_products
    ADD CONSTRAINT fk_restaurant_products_on_product FOREIGN KEY (product_id)
        REFERENCES restaurant.products (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
        NOT VALID;

-- ################################################################
-- ORDER APPROVALS TABLE
-- ################################################################
DROP TYPE IF EXISTS approval_status;
CREATE TYPE approval_status AS ENUM ('APPROVED', 'REJECTED');

DROP TABLE IF EXISTS restaurant.order_approvals;
CREATE TABLE restaurant.order_approvals
(
    id            UUID            NOT NULL,
    restaurant_id UUID            NOT NULL,
    order_id      UUID            NOT NULL,
    status        approval_status NOT NULL,
    CONSTRAINT pk_restaurant_approvals PRIMARY KEY (id)
);

-- ################################################################
-- ORDER RESTAURANT MATERIALIZED VIEW
-- ################################################################
DROP MATERIALIZED VIEW IF EXISTS restaurant.order_restaurant_m_view;

CREATE MATERIALIZED VIEW restaurant.order_restaurant_m_view TABLESPACE pg_default AS
SELECT r.id        AS restaurant_id,
       r.name      AS restaurant_name,
       r.active    AS restaurant_active,
       p.id        AS product_id,
       p.name      AS product_name,
       p.price     AS product_price,
       p.available AS product_available
FROM restaurant.restaurants r
         join restaurant.restaurant_products rp on r.id = rp.restaurant_id
         join restaurant.products p on rp.product_id = p.id
WITH DATA;

REFRESH MATERIALIZED VIEW restaurant.order_restaurant_m_view;

-- FUNCTION TO REFRESH MATERIALIZED VIEW
DROP FUNCTION IF EXISTS restaurant.refresh_order_restaurant_m_view();

CREATE OR REPLACE FUNCTION restaurant.refresh_order_restaurant_m_view()
    RETURNS trigger
AS
'
    BEGIN
        REFRESH MATERIALIZED VIEW restaurant.order_restaurant_m_view;
        RETURN NULL;
    END;
' LANGUAGE plpgsql;

-- TRIGGER TO REFRESH MATERIALIZED VIEW
DROP TRIGGER IF EXISTS refresh_order_restaurant_m_view_trigger ON restaurant.restaurant_products;

CREATE TRIGGER refresh_order_restaurant_m_view_trigger
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE
    ON restaurant.restaurant_products
    FOR EACH STATEMENT
EXECUTE PROCEDURE restaurant.refresh_order_restaurant_m_view();
