DROP DATABASE IF EXISTS pastry_db;

CREATE DATABASE IF NOT EXISTS pastry_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE pastry_db;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255)  ,
    phone VARCHAR(20) ,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type ENUM('customer', 'restaurant_owner', 'admin') NOT NULL,
    profile_image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_user_type (user_type),
    INDEX idx_users_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE customer (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    fullname VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50),
    postal_code VARCHAR(20),
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    CONSTRAINT fk_customer_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE restaurants (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    cuisine_type VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(255),
    address_line1 VARCHAR(255) ,
    address_line2 VARCHAR(255),
    city VARCHAR(100) ,
    state VARCHAR(50),
    postal_code VARCHAR(20),
    website VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_accepting_orders BOOLEAN NOT NULL DEFAULT TRUE,
    min_order_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    delivery_fee DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    rating DECIMAL(3, 2) NOT NULL DEFAULT 0.00,
    total_reviews INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_restaurant_owner
        FOREIGN KEY (owner_id) REFERENCES users(id),

    CONSTRAINT chk_restaurant_rating
        CHECK (rating BETWEEN 0.00 AND 5.00),

    CONSTRAINT chk_restaurant_reviews
        CHECK (total_reviews >= 0),

    CONSTRAINT uq_restaurant_location
        UNIQUE (name, address_line1, city, postal_code),

    CONSTRAINT uq_restaurant_location2
        UNIQUE (name, address_line2, city, postal_code),

    INDEX idx_restaurants_owner (owner_id),
    INDEX idx_restaurants_cuisine (cuisine_type),
    INDEX idx_restaurants_rating (rating),
    INDEX idx_restaurants_active (is_active, is_accepting_orders)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;



CREATE TABLE menu_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_category_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
        ON DELETE CASCADE,
    INDEX idx_categories_restaurant (restaurant_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE menu_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    restaurant_id INT NOT NULL,
    category_id INT,
    name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discounted_price DECIMAL(10, 2),
    preparation_time INT,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_item_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_item_category
        FOREIGN KEY (category_id) REFERENCES menu_categories(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_item_quantity
        CHECK (quantity >= 0),
    CONSTRAINT chk_item_price
        CHECK (price >= 0.00),
    CONSTRAINT chk_item_discounted_price
        CHECK (discounted_price IS NULL OR (discounted_price >= 0.00 AND discounted_price <= price)),
    CONSTRAINT chk_item_preparation_time
        CHECK (preparation_time IS NULL OR preparation_time >= 0),
    INDEX idx_items_restaurant_category (restaurant_id, category_id),
    INDEX idx_items_available (is_available),
    INDEX idx_items_price (price)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE orders (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    restaurant_id INT NOT NULL,
    status ENUM(
        'pending',
        'confirmed',
        'preparing',
        'ready_for_pickup',
        'on_the_way',
        'delivered',
        'cancelled'
    ) NOT NULL DEFAULT 'pending',
    subtotal DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM(
        'credit_card',
        'debit_card',
        'cash',
        'wallet',
        'paypal'
    ) NOT NULL,
    payment_intent_id VARCHAR(255),
    special_instructions TEXT,
    actual_delivery_time TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP NULL,
    prepared_at TIMESTAMP NULL,
    picked_up_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    CONSTRAINT fk_order_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_order_subtotal
        CHECK (subtotal >= 0.00),
    CONSTRAINT chk_order_discount
        CHECK (discount_amount >= 0.00),
    CONSTRAINT chk_order_total
        CHECK (total_amount >= 0.00),
    INDEX idx_orders_customer (customer_id),
    INDEX idx_orders_restaurant (restaurant_id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE order_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    special_instructions TEXT,
    selected_options JSON,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_item_menu_item
        FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_order_item_quantity
        CHECK (quantity > 0),
    CONSTRAINT chk_order_item_unit_price
        CHECK (unit_price >= 0.00),
    CONSTRAINT chk_order_item_total_price
        CHECK (total_price >= 0.00),
    INDEX idx_order_items_order (order_id),
    INDEX idx_order_items_menu_item (menu_item_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;






DELIMITER $$

CREATE TRIGGER trg_restaurants_owner_insert
BEFORE INSERT ON restaurants
FOR EACH ROW
BEGIN
    DECLARE owner_type VARCHAR(50);

    SELECT user_type
    INTO owner_type
    FROM users
    WHERE id = NEW.owner_id;

    IF owner_type IS NULL OR owner_type <> 'restaurant_owner' THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Restaurant owner must be a restaurant_owner user';
    END IF;
END$$

CREATE TRIGGER trg_restaurants_owner_update
BEFORE UPDATE ON restaurants
FOR EACH ROW
BEGIN
    DECLARE owner_type VARCHAR(50);

    IF NEW.owner_id <> OLD.owner_id THEN
        SELECT user_type
        INTO owner_type
        FROM users
        WHERE id = NEW.owner_id;

        IF owner_type IS NULL OR owner_type <> 'restaurant_owner' THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Restaurant owner must be a restaurant_owner user';
        END IF;
    END IF;
END$$

CREATE TRIGGER trg_users_owner_type_update
BEFORE UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.user_type = 'restaurant_owner'
       AND NEW.user_type <> 'restaurant_owner'
       AND EXISTS (
           SELECT 1
           FROM restaurants
           WHERE owner_id = OLD.id
       ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'A user who owns a restaurant must remain a restaurant_owner';
    END IF;
END$$

DELIMITER ;

DELIMITER $$

CREATE TRIGGER trg_order_items_check_and_decrement_quantity
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    DECLARE available_quantity INT;

    SELECT quantity
    INTO available_quantity
    FROM menu_items
    WHERE id = NEW.menu_item_id
    FOR UPDATE;

    IF available_quantity IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'The referenced menu item does not exist';
    ELSEIF NEW.quantity > available_quantity THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient menu item quantity';
    ELSE
        UPDATE menu_items
        SET quantity = quantity - NEW.quantity
        WHERE id = NEW.menu_item_id;
    END IF;
END$$

DELIMITER ;

-- Seed users and sample catalogue data for local development.
INSERT INTO users (email, phone, username, password, user_type)
VALUES
    ('admin@pastry.local', '1000000001', 'admin_user', 'admin123', 'admin'),
    ('customer@pastry.local', '1000000002', 'customer_user', 'customer123', 'customer'),
    ('owner@pastry.local', '1000000003', 'store_manager', 'owner123', 'restaurant_owner');

INSERT INTO customer (
    user_id,
    fullname,
    city,
    state,
    postal_code,
    address_line1
)
SELECT id, 'Sample Customer', 'Pastryville', 'PA', '10001', '1 Sweet Street'
FROM users
WHERE username = 'customer_user';

INSERT INTO restaurants (
    owner_id,
    name,
    description,
    address_line1,
    city,
    state,
    postal_code,
    cuisine_type
)
SELECT id,
       'The Pastry Corner',
       'A small bakery serving fresh pastries and coffee.',
       '10 Baker Street',
       'Pastryville',
       'PA',
       '10001',
       'Bakery'
FROM users
WHERE username = 'store_manager';

INSERT INTO menu_categories (restaurant_id, name, description)
SELECT id, 'Pastries', 'Freshly baked sweet pastries.'
FROM restaurants
WHERE name = 'The Pastry Corner';

INSERT INTO menu_categories (restaurant_id, name, description)
SELECT id, 'Drinks', 'Hot and cold drinks to enjoy with your pastry.'
FROM restaurants
WHERE name = 'The Pastry Corner';

INSERT INTO menu_items (restaurant_id, category_id, name, quantity, description, price)
SELECT restaurant.id, category.id, item.name, item.quantity, item.description, item.price
FROM restaurants AS restaurant
JOIN menu_categories AS category ON category.restaurant_id = restaurant.id
JOIN (
    SELECT 'Croissant' AS name, 25 AS quantity,
           'Butter croissant baked fresh each morning.' AS description, 3.50 AS price,
           'Pastries' AS category_name
    UNION ALL
    SELECT 'Apple Tart', 20,
           'Golden apple tart with a flaky pastry crust.', 4.25,
           'Pastries'
    UNION ALL
    SELECT 'Cappuccino', 30,
           'Espresso with steamed milk and foam.', 3.75,
           'Drinks'
    UNION ALL
    SELECT 'Iced Tea', 30,
           'Refreshing house-brewed iced tea.', 2.50,
           'Drinks'
) AS item ON item.category_name = category.name
WHERE restaurant.name = 'The Pastry Corner';
