-- ################################################################
-- SCHEMA
-- ################################################################
DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;


-- ################################################################
-- EXTENSIONS
-- ################################################################

-- uuid-ossp
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- ################################################################
-- TYPES
-- ################################################################

-- payment status
DROP TYPE IF EXISTS payment_status;
CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');

-- transaction type
DROP TYPE IF EXISTS transaction_type;
CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT');


-- ################################################################
-- TABLES
-- ################################################################

-- payments
DROP TABLE IF EXISTS payment.payments;
CREATE TABLE payment.payments
(
    id             UUID                     NOT NULL,
    customer_id    UUID                     NOT NULL,
    order_id       UUID                     NOT NULL,
    price          NUMERIC(10, 2)           NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_status payment_status           NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

-- credit entries
DROP TABLE IF EXISTS payment.credit_entries;
CREATE TABLE payment.credit_entries
(
    id                  UUID           NOT NULL,
    customer_id         UUID           NOT NULL,
    total_credit_amount NUMERIC(10, 2) NOT NULL,
    CONSTRAINT pk_credit_entry PRIMARY KEY (id)
);

-- credit histories
DROP TABLE IF EXISTS payment.credit_histories;
CREATE TABLE payment.credit_histories
(
    id               UUID             NOT NULL,
    customer_id      UUID             NOT NULL,
    amount           NUMERIC(10, 2)   NOT NULL,
    transaction_type transaction_type NOT NULL,
    CONSTRAINT pk_credit_history PRIMARY KEY (id)
);


-- ################################################################
-- OUTBOX TYPES
-- ################################################################

-- outbox status
DROP TYPE IF EXISTS outbox_status;
CREATE TYPE outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');


-- ################################################################
-- OUTBOX TABLES
-- ################################################################

-- order outbox
DROP TABLE IF EXISTS payment.order_outbox CASCADE;
CREATE TABLE payment.order_outbox
(
    id             UUID                                           NOT NULL,
    saga_id        UUID                                           NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE                       NOT NULL,
    processed_at   TIMESTAMP WITH TIME ZONE,
    type           CHARACTER VARYING COLLATE pg_catalog."default" NOT NULL,
    payload        JSONB                                          NOT NULL,
    outbox_status  outbox_status                                  NOT NULL,
    payment_status payment_status                                 NOT NULL,
    version        INTEGER                                        NOT NULL,
    CONSTRAINT pk_order_outbox PRIMARY KEY (id)
);
CREATE INDEX "idx_order_outbox_status"
    ON "payment".order_outbox (type, payment_status);
CREATE UNIQUE INDEX "idx_order_outbox_saga_id"
    ON "payment".order_outbox (type, saga_id, payment_status, outbox_status);
