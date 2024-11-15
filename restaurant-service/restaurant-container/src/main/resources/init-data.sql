-- RESTAURANTS
INSERT INTO restaurant.restaurants(id, name, active)
VALUES ('fe8330d4-4958-4038-8296-925847efeba1', 'Restaurant 1', TRUE),
       ('fe8330d4-4958-4038-8296-925847efeba2', 'Restaurant 2', FALSE);

-- PRODUCTS
INSERT INTO restaurant.products(id, name, price, available)
VALUES ('0201e512-ea8f-403c-8c82-499a5f764ad1', 'Product 1', 25.0, FALSE),
       ('0201e512-ea8f-403c-8c82-499a5f764ad2', 'Product 2', 50.0, TRUE),
       ('0201e512-ea8f-403c-8c82-499a5f764ad3', 'Product 3', 20.0, FALSE),
       ('0201e512-ea8f-403c-8c82-499a5f764ad4', 'Product 4', 40.0, TRUE);

-- RESTAURANT PRODUCTS
INSERT INTO restaurant.restaurant_products(id, restaurant_id, product_id)
VALUES ('0201e512-ea8f-403c-8c82-499a5f764ad5', 'fe8330d4-4958-4038-8296-925847efeba1', '0201e512-ea8f-403c-8c82-499a5f764ad1'),
       ('0201e512-ea8f-403c-8c82-499a5f764ad6', 'fe8330d4-4958-4038-8296-925847efeba1', '0201e512-ea8f-403c-8c82-499a5f764ad2'),
       ('0201e512-ea8f-403c-8c82-499a5f764ad7', 'fe8330d4-4958-4038-8296-925847efeba2', '0201e512-ea8f-403c-8c82-499a5f764ad3'),
       ('0201e512-ea8f-403c-8c82-499a5f764ad8', 'fe8330d4-4958-4038-8296-925847efeba2', '0201e512-ea8f-403c-8c82-499a5f764ad4');
