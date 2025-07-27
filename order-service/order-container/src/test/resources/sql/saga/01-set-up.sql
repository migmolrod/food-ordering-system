INSERT INTO "order".orders(id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages)
VALUES ('b9434aae-b91c-4b76-89a4-da6e48f3c185',
        'fe1a2605-3471-46c5-9283-d283b4fa1477',
        '25da4b9a-80cc-4aae-9ccd-40a5c600393a',
        '049803d0-6efc-4631-b118-3fd589f8cace',
        200.00,
        'PENDING',
        '');

INSERT INTO "order".order_items(id, order_id, product_id, price, quantity, sub_total)
VALUES (1,
        'b9434aae-b91c-4b76-89a4-da6e48f3c185',
        '92624dff-93b2-4f7a-a422-caa18b1a6de3',
        100.00,
        2,
        200.00);

INSERT INTO "order".order_addresses(id, order_id, street, postal_code, city)
VALUES ('8b0fb960-2f03-48e9-b1e8-989e8e32dd52',
        'b9434aae-b91c-4b76-89a4-da6e48f3c185',
        'Test Street, 1st',
        '1000AA',
        'Test City');

INSERT INTO "order".payment_outbox(id, saga_id, created_at, type, payload, outbox_status, saga_status, order_status,
                                   version)
VALUES ('751d1e5f-c9bc-4fef-9ed2-dc34555dcd46',
        '55da690a-f9b1-4b96-b079-58d352f0c545',
        current_timestamp,
        'OrderProcessingSaga',
        '{"price":200, "orderId":"22897b18-ee4d-469c-b826-92e21deee992", "createdAt":"2021-01-01T00:00:00.000Z", "customerId":"fe1a2605-3471-46c5-9283-d283b4fa1477", "paymentOrderStatus":"PENDING"}',
        'STARTED',
        'STARTED',
        'PENDING',
        0);
