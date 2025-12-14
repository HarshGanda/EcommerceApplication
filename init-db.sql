-- Initialize databases for all microservices
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS catalog_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS cart_db;
CREATE DATABASE IF NOT EXISTS payment_db;

-- Use auth_db and create user table
USE auth_db;

-- Use catalog_db and create tables
USE catalog_db;

-- Use order_db and create tables
USE order_db;

-- Use cart_db and create tables
USE cart_db;

-- Use payment_db and create tables
USE payment_db;

-- Grant privileges
GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON catalog_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON order_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON cart_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON payment_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

