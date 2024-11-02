DROP SCHEMA IF EXISTS "order" CASCADE;

CREATE SCHEMA "order";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS order_status;
CREATE TYPE order_status AS ENUM ('PENDING','PAID','APPROVED','CANCELLING','CANCELLED');

DROP TABLE IF EXISTS "order".orders;
CREATE TABLE "order".orders
(
    id               UUID           NOT NULL,
    customer_id      UUID           NOT NULL,
    restaurant_id    UUID           NOT NULL,
    tracking_id      UUID           NOT NULL,
    price            NUMERIC(10, 2) NOT NULL,
    order_status     order_status   NOT NULL,
    failure_messages CHARACTER VARYING COLLATE pg_catalog."default",
    CONSTRAINT pk_order PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "order".order_items;
CREATE TABLE "order".order_items
(
    id         BIGINT         NOT NULL,
    order_id   UUID           NOT NULL,
    product_id UUID           NOT NULL,
    price      NUMERIC(10, 2) NOT NULL,
    quantity   INTEGER        NOT NULL,
    sub_total  NUMERIC(10, 2) NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id, order_id)
);

ALTER TABLE "order".order_items
    ADD CONSTRAINT fk_order_items_order_id FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;

DROP TABLE IF EXISTS "order".order_addresses;
CREATE TABLE "order".order_addresses
(
    id          UUID                                           NOT NULL,
    order_id    UUID                                           NOT NULL,
    street      CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    postal_code CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    city        CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT pk_order_address PRIMARY KEY (id, order_id)
);

ALTER TABLE "order".order_addresses
    ADD CONSTRAINT fk_order_address_order_id FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;
