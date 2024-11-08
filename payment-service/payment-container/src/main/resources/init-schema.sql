DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS payment_status;
CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED');

DROP TABLE IF EXISTS payment.payments;
CREATE TABLE payment.payments
(
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    order_id UUID NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    payment_status payment_status NOT NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);

DROP TABLE IF EXISTS payment.credit_entries;
CREATE TABLE payment.credit_entries
(
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    total_credit_amount NUMERIC(10, 2) NOT NULL,
    CONSTRAINT pk_credit_entry PRIMARY KEY (id)
);

DROP TYPE IF EXISTS transaction_type;
CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT');

DROP TABLE IF EXISTS payment.credit_histories;
CREATE TABLE payment.credit_histories
(
    id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    transaction_type transaction_type NOT NULL,
    CONSTRAINT pk_credit_history PRIMARY KEY (id)
);
