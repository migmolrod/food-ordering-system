-- CUSTOMER 1
INSERT INTO payment.credit_entries(id, customer_id, total_credit_amount)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb41', 'd215b5f8-0249-4dc5-89a3-51fd148cfb55', 500.00);
INSERT INTO payment.credit_histories(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb43', 'd215b5f8-0249-4dc5-89a3-51fd148cfb55', 100.00, 'CREDIT'),
       ('d215b5f8-0249-4dc5-89a3-51fd148cfb44', 'd215b5f8-0249-4dc5-89a3-51fd148cfb55', 600.00, 'CREDIT'),
       ('d215b5f8-0249-4dc5-89a3-51fd148cfb45', 'd215b5f8-0249-4dc5-89a3-51fd148cfb55', 200.00, 'DEBIT');

-- CUSTOMER 2
INSERT INTO payment.credit_entries(id, customer_id, total_credit_amount)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb42', 'd215b5f8-0249-4dc5-89a3-51fd148cfb56', 100.00);
INSERT INTO payment.credit_histories(id, customer_id, amount, transaction_type)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb46', 'd215b5f8-0249-4dc5-89a3-51fd148cfb56', 100.00, 'CREDIT');
