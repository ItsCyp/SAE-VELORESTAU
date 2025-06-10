CREATE DATABASE IF NOT EXISTS velorestau;
USE velorestau;

CREATE TABLE IF NOT EXISTS restaurants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    party_size INT NOT NULL,
    reservation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);
CREATE TABLE IF NOT EXISTS restaurant_tables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    table_number INT NOT NULL,
    seats INT NOT NULL,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants(id)
);

-- Sample restaurants located in Nancy
INSERT INTO restaurants (name, address, phone, latitude, longitude) VALUES
('Le Gourmet', '10 Rue des Ponts, 54000 Nancy', '03 83 11 22 33', 48.6937, 6.1834),
('Bistro des Arts', '5 Place Stanislas, 54000 Nancy', '03 83 44 55 66', 48.6933, 6.1839),
('La Table Lorraine', '15 Rue Saint-Georges, 54000 Nancy', '03 83 77 88 99', 48.6920, 6.1836);

