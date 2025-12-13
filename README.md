# E-Commerce Microservices Application

A comprehensive microservices-based e-commerce platform built with Spring Boot, demonstrating modern distributed architecture patterns.

## Architecture Overview

This project consists of the following microservices:

### Core Services

1. **Discovery Service** (Port 8761)
   - Eureka Server for service registration and discovery
   - Provides a dashboard to monitor all registered services
   - Health monitoring for all microservices

2. **Auth Service** (Port 9000)
   - User authentication and authorization
   - JWT token generation and validation
   - OAuth2 support
   - User registration and management
   - Password encryption with BCrypt
   - Kafka integration for user registration notifications

3. **Catalog Service** (Port 8083)
   - Product catalog management (CRUD operations)
   - Category management (CRUD operations)
   - Advanced product search with filters (name, price range, category)
   - Pagination and sorting support
   - Redis caching for improved performance (10-minute TTL)
   - Product inventory management (stock tracking)
   - Product images, branding, and ratings

4. **Cart Service** (Port 8086)
   - Shopping cart management
   - Add/update/remove items from cart
   - Automatic total calculation
   - Clear cart functionality
   - Redis caching for cart data
   - Prevents duplicate items

5. **Order Service** (Port 8084)
   - Order creation and management with multiple items
   - Order status tracking (6 states: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
   - Order history by user
   - Update order status
   - Cancel orders
   - Kafka integration for order notifications
   - Automatic timestamps

6. **Payment Gateway Service** (Port 8085)
   - Payment processing via Razorpay and Stripe
   - Payment transaction persistence
   - Payment history tracking
   - Multiple payment status states
   - Webhook handling for payment events
   - Transaction database

7. **Notification Service** (Port 8082)
   - Email notification service
   - Kafka consumer for async notifications
   - JavaMail integration with SMTP
   - Sends emails for user registration, order confirmation, etc.
   - Configurable email templates

## Technology Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.2.3
- **Service Discovery:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway (optional)
- **Database:** MySQL
- **Caching:** Redis
- **Messaging:** Apache Kafka
- **Security:** Spring Security, JWT
- **Build Tool:** Maven
- **Containerization:** Docker (optional)

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Apache Kafka 3.0+
- (Optional) Docker and Docker Compose

## Database Setup

Create the following databases in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS catalog_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS cart_db;
CREATE DATABASE IF NOT EXISTS payment_db;
```

Alternatively, you can use the provided `init-db.sql` script that will automatically create all databases when using Docker Compose.

## Configuration

Update the following files with your local configurations:

### 1. Auth Service
`auth-service/src/main/resources/application.properties`
- Database credentials (auth_db)
- JWT secret key
- Kafka server address

### 2. Catalog Service
`catalog-service/src/main/resources/application.properties`
- Database credentials (catalog_db)
- Redis host and port
- Kafka server address

### 3. Cart Service
`cart-service/src/main/resources/application.properties`
- Database credentials (cart_db)
- Redis host and port

### 4. Order Service
`order-service/src/main/resources/application.properties`
- Database credentials (order_db)
- Kafka server address

### 5. Notification Service
`notification-service/src/main/resources/application.properties`
- Email SMTP configuration
- Kafka server address

### 6. Payment Gateway Service
`payment-gateway-service/src/main/resources/application.properties`
- Database credentials (payment_db)
- Razorpay API keys
- Stripe API keys

## Running the Application

### Step 1: Start Infrastructure Services

Ensure MySQL, Redis, and Kafka are running on your local machine.

**Option A: Using Docker Compose (Recommended)**
```bash
docker-compose up -d
```

**Option B: Manual Setup**
- Start MySQL (Port 3306)
- Start Redis (Port 6379)
- Start Zookeeper (Port 2181)
- Start Kafka (Port 9092)

### Step 2: Build All Services

From the project root directory:

```bash
mvn clean install
```

### Step 3: Start Services in Order

1. **Start Discovery Service first:**
   ```bash
   cd discovery-service
   mvn spring-boot:run
   ```
   Wait for it to start completely (check http://localhost:8761)

2. **Start other services (order doesn't matter):**
   
   **On Windows (PowerShell/CMD) - Open separate terminals for each:**
   ```powershell
   cd auth-service ; mvn spring-boot:run
   cd catalog-service ; mvn spring-boot:run
   cd cart-service ; mvn spring-boot:run
   cd order-service ; mvn spring-boot:run
   cd payment-gateway-service ; mvn spring-boot:run
   cd notification-service ; mvn spring-boot:run
   ```
   
   **On Linux/Mac - Open separate terminals for each:**
   ```bash
   cd auth-service && mvn spring-boot:run
   cd catalog-service && mvn spring-boot:run
   cd cart-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-gateway-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

## API Documentation

### Auth Service (Port 9000)

#### Register User
```http
POST /users/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "role": "USER"
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Validate Token
```http
POST /auth/validate
Content-Type: application/json

{
  "token": "your-jwt-token"
}
```

### Catalog Service (Port 8083)

#### Get All Products
```http
GET /products
```

#### Get Product by ID
```http
GET /products/{id}
```

#### Create Product
```http
POST /products
Content-Type: application/json

{
  "name": "Product Name",
  "description": "Product Description",
  "price": 99.99,
  "stockQuantity": 100,
  "imageUrl": "https://example.com/image.jpg",
  "brand": "Brand Name",
  "rating": 4.5,
  "category": {
    "id": 1,
    "name": "Category Name"
  }
}
```

#### Update Product
```http
PUT /products/{id}
Content-Type: application/json

{
  "name": "Updated Product Name",
  "description": "Updated Description",
  "price": 89.99,
  "stockQuantity": 150,
  "imageUrl": "https://example.com/new-image.jpg",
  "brand": "Brand Name",
  "rating": 4.7,
  "category": {
    "id": 1,
    "name": "Category Name"
  }
}
```

#### Delete Product
```http
DELETE /products/{id}
```

#### Search Products (with filters, pagination, and sorting)
```http
GET /search/products?name=laptop&minPrice=100&maxPrice=2000&categoryId=1&page=0&size=10&sortBy=price&sortDirection=asc
```

#### Get Products by Category
```http
GET /search/products/by-category/{categoryId}
```

#### Get Products by Price Range
```http
GET /search/products/by-price-range?minPrice=100&maxPrice=500
```

#### Get All Categories
```http
GET /categories
```

#### Get Category by ID
```http
GET /categories/{id}
```

#### Create Category
```http
POST /categories
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic items"
}
```

#### Update Category
```http
PUT /categories/{id}
Content-Type: application/json

{
  "name": "Updated Category",
  "description": "Updated description"
}
```

#### Delete Category
```http
DELETE /categories/{id}
```

### Cart Service (Port 8086)

#### Get Cart by User ID
```http
GET /cart/user/{userId}
```

#### Add Item to Cart
```http
POST /cart/add
Content-Type: application/json

{
  "userId": 1,
  "productId": 1,
  "productName": "Product Name",
  "quantity": 2,
  "price": 99.99
}
```

#### Update Cart Item Quantity
```http
PUT /cart/update/{cartItemId}
Content-Type: application/json

{
  "quantity": 3
}
```

#### Remove Item from Cart
```http
DELETE /cart/remove/{cartItemId}
```

#### Clear Cart
```http
DELETE /cart/clear/{userId}
```

### Order Service (Port 8084)

#### Create Order
```http
POST /orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "productName": "Product Name",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "totalAmount": 199.98,
  "shippingAddress": "123 Main St, City, Country"
}
```

#### Get Order by ID
```http
GET /orders/{id}
```

#### Get Orders by User
```http
GET /orders/user/{userId}
```

#### Update Order Status
```http
PUT /orders/{id}/status
Content-Type: application/json

{
  "status": "SHIPPED"
}
```

#### Cancel Order
```http
DELETE /orders/{id}
```

### Payment Gateway Service (Port 8085)

#### Initiate Payment
```http
POST /payments/initiate
Content-Type: application/json

{
  "userId": "1",
  "orderId": 1,
  "amount": 199.98,
  "currency": "USD",
  "method": "razorpay"
}
```

#### Get Payment Status
```http
GET /payments/status/{paymentId}
```

#### Get Payment History by User
```http
GET /payments/user/{userId}
```

## Monitoring

- **Eureka Dashboard:** http://localhost:8761
  - View all registered services and their health status

## Kafka Topics

The application uses the following Kafka topics:

- `notification-events`: For sending email notifications

## Project Structure

```
EcommerceApplication/
├── auth-service/
├── catalog-service/
├── cart-service/
├── order-service/
├── payment-gateway-service/
├── notification-service/
├── discovery-service/
├── docker-compose.yml
├── init-db.sql
├── pom.xml (parent)
└── README.md
```

## Features Implemented

✅ User Authentication & Authorization (JWT)
✅ Service Discovery (Eureka)
✅ Product Catalog Management
✅ Category Management
✅ Advanced Product Search with Filters
✅ Redis Caching (Products, Categories, Cart)
✅ Shopping Cart Management
✅ Order Management with Multiple Items
✅ Order Status Tracking (6 States)
✅ Payment Gateway Integration (Razorpay & Stripe)
✅ Payment Transaction Persistence
✅ Email Notifications (Kafka + JavaMail)
✅ RESTful APIs
✅ Exception Handling
✅ DTO Pattern
✅ Docker Compose Setup
✅ Database Initialization Script

## Future Enhancements

- API Gateway implementation
- Circuit Breaker pattern (Resilience4j)
- Distributed tracing (Sleuth + Zipkin)
- Centralized configuration (Spring Cloud Config)
- Kubernetes deployment
- Product Reviews & Ratings service
- Admin Dashboard
- Real-time Order Tracking
- Product Recommendations Engine
- User Wishlist service
- Multi-language Support

## Troubleshooting

### Port Already in Use
If a port is already in use, you can change it in the respective service's `application.properties` file.

### Database Connection Issues
Ensure MySQL is running and the databases are created. Check the credentials in application.properties.

### Kafka Connection Issues
Ensure Kafka and Zookeeper are running. Default connection is `localhost:9092`.

### Redis Connection Issues
Ensure Redis is running on `localhost:6379`.

## License

This project is for educational purposes.

## Contributors

Development Team

---

For any issues or questions, please create an issue in the repository.

