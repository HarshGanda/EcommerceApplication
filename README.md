# E-Commerce Microservices Application

A comprehensive microservices-based e-commerce platform built with Spring Boot, demonstrating modern distributed architecture patterns and best practices.

## Architecture Overview

This project consists of the following microservices:

### Core Services

1. **Discovery Service** (Port 8761)
   - Eureka Server for service registration and discovery
   - Provides a dashboard to monitor all registered services
   - Health monitoring for all microservices
   - Does not self-register

2. **Auth Service** (Port 9000)
   - User authentication and authorization
   - JWT token generation and validation
   - OAuth2 support with Spring Authorization Server
   - User registration and management
   - Password encryption with BCrypt
   - Kafka integration for user registration notifications
   - Custom exception handling
   - Comprehensive test coverage with JaCoCo

3. **Catalog Service** (Port 8083)
   - Product catalog management (CRUD operations)
   - Category management (CRUD operations)
   - Advanced product search with filters (name, price range, category)
   - Pagination and sorting support
   - Redis caching for improved performance (10-minute TTL)
   - Product inventory management (stock tracking)
   - Product images, branding, and ratings
   - Global exception handling

4. **Cart Service** (Port 8086)
   - Shopping cart management
   - Add/update/remove items from cart
   - Automatic total calculation
   - Clear cart functionality
   - Redis caching for cart data
   - Prevents duplicate items
   - Integration test coverage

5. **Order Service** (Port 8084)
   - Order creation and management with multiple items
   - Order status tracking (6 states: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
   - Order history by user
   - Update order status
   - Cancel orders
   - Kafka integration for order notifications
   - Automatic timestamps
   - JaCoCo test coverage reports

6. **Payment Gateway Service** (Port 8085)
   - Payment processing via Razorpay and Stripe
   - Payment transaction persistence
   - Payment history tracking
   - Multiple payment status states
   - Webhook handling for payment events
   - Transaction database
   - Comprehensive test coverage with JaCoCo

7. **Notification Service** (Port 8083)
   - Email notification service
   - Kafka consumer for async notifications
   - JavaMail integration with SMTP
   - Sends emails for user registration, order confirmation, etc.
   - Configurable email templates
   - Listens to `notification-events` topic

> **Note:** Notification Service currently uses port 8083, which conflicts with Catalog Service. Consider changing to port 8082 for production use.

## Technology Stack

### Core Technologies
- **Language:** Java 17
- **Framework:** Spring Boot 3.2.3
- **Spring Cloud:** 2023.0.0
- **Build Tool:** Maven

### Infrastructure
- **Service Discovery:** Netflix Eureka
- **Database:** MySQL 8.0+
- **Caching:** Redis 6.0+
- **Messaging:** Apache Kafka 3.0+
- **Containerization:** Docker & Docker Compose

### Security & Integration
- **Security:** Spring Security, JWT (jjwt 0.9.1), OAuth2 Authorization Server
- **Email:** JavaMail (SMTP)
- **Payment Gateways:** Razorpay, Stripe

### Development Tools
- **Testing:** JUnit, Spring Boot Test, H2 (in-memory for tests)
- **Code Coverage:** JaCoCo 0.8.11
- **Dev Tools:** Spring Boot DevTools, Lombok
- **Validation:** Spring Boot Starter Validation

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

Each service has its own `application.properties` file that needs to be configured. Below are the key configuration files:

### 1. Auth Service
`auth-service/src/main/resources/application.properties`
- **Database:** auth_db on MySQL (localhost:3306)
- **JWT Secret:** Configurable secret key for token generation
- **JWT Expiration:** 86400000 ms (24 hours)
- **Kafka:** localhost:9092
- **Eureka:** Registers with Discovery Service

**Key Properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
jwt.secret=ecommerce-app-super-secret-key-for-jwt-token-generation-2025
jwt.expiration=86400000
spring.kafka.bootstrap-servers=localhost:9092
```

### 2. Catalog Service
`catalog-service/src/main/resources/application.properties`
- **Database:** catalog_db on MySQL (localhost:3306)
- **Redis:** localhost:6379 (10-minute cache TTL)
- **Eureka:** Registers with Discovery Service

**Key Properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/catalog_db
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.redis.time-to-live=600000
```

### 3. Cart Service
`cart-service/src/main/resources/application.properties`
- **Database:** cart_db on MySQL (localhost:3306)
- **Redis:** localhost:6379 for cart caching
- **Eureka:** Registers with Discovery Service

**Key Properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cart_db
spring.data.redis.host=localhost
```

### 4. Order Service
`order-service/src/main/resources/application.properties`
- **Database:** order_db on MySQL (localhost:3306)
- **Kafka:** localhost:9092 for order notifications
- **Eureka:** Registers with Discovery Service

**Key Properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/order_db
spring.kafka.bootstrap-servers=localhost:9092
```

### 5. Notification Service
`notification-service/src/main/resources/application.properties`
- **Email SMTP:** Gmail SMTP server (smtp.gmail.com:587)
- **Kafka:** Consumes from localhost:9092
- **Eureka:** Registers with Discovery Service

> **Important:** Update email credentials before running
> **Port Conflict:** Currently set to 8083 (conflicts with Catalog Service)

**Key Properties:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@example.com
spring.mail.password=your_app_password
notification.from.email=noreply@ecommerce.com
```

### 6. Payment Gateway Service
`payment-gateway-service/src/main/resources/application.properties`
- **Database:** payment_db on MySQL (localhost:3306)
- **Payment Gateways:** Razorpay and Stripe API integration
- **Eureka:** Registers with Discovery Service

**Key Properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/payment_db
# Add your Razorpay and Stripe API keys
```

### 7. Discovery Service
`discovery-service/src/main/resources/application.properties`
- **Port:** 8761
- **Eureka:** Standalone server (does not self-register)

**Key Properties:**
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

## Running the Application

### Step 1: Start Infrastructure Services

Ensure MySQL, Redis, and Kafka are running on your local machine.

**Option A: Using Docker Compose (Recommended)**
```bash
docker-compose up -d
```

This will start:
- MySQL 8.0 on port 3306 (with automatic database initialization from `init-db.sql`)
- Redis 7 on port 6379
- Zookeeper on port 2181
- Kafka on port 9092

**Option B: Manual Setup**
- Start MySQL (Port 3306) and create databases using `init-db.sql`
- Start Redis (Port 6379)
- Start Zookeeper (Port 2181)
- Start Kafka (Port 9092)

### Step 2: Build All Services

From the project root directory:

```bash
mvn clean install
```

This will:
- Build all 7 microservices
- Run all unit and integration tests
- Generate JaCoCo code coverage reports (for auth, order, and payment services)
- Create executable JAR files in each service's `target/` directory

### Step 3: Start Services in Order

1. **Start Discovery Service first:**
   ```bash
   cd discovery-service
   mvn spring-boot:run
   ```
   Wait for it to start completely (check http://localhost:8761)

2. **Start other services (in separate terminals):**
   
   **Windows (PowerShell) - Each in a new terminal:**
   ```powershell
   cd auth-service ; mvn spring-boot:run
   cd catalog-service ; mvn spring-boot:run
   cd cart-service ; mvn spring-boot:run
   cd order-service ; mvn spring-boot:run
   cd payment-gateway-service ; mvn spring-boot:run
   cd notification-service ; mvn spring-boot:run
   ```
   
   **Linux/Mac - Each in a new terminal:**
   ```bash
   cd auth-service && mvn spring-boot:run
   cd catalog-service && mvn spring-boot:run
   cd cart-service && mvn spring-boot:run
   cd order-service && mvn spring-boot:run
   cd payment-gateway-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   ```

> **Note:** If you encounter port conflicts, ensure no other services are using ports 8761, 9000, 8083, 8084, 8085, 8086. The notification-service currently uses port 8083 which conflicts with catalog-service.

### Verification

After all services are started:
1. Visit **Eureka Dashboard**: http://localhost:8761
2. Verify all services are registered
3. Check service health status
4. Each service should appear with its application name

## Testing

The project includes comprehensive unit and integration tests for all services.

### Running Tests

**Run all tests:**
```bash
mvn test
```

**Run tests for a specific service:**
```bash
cd auth-service
mvn test
```

**Run tests with coverage report:**
```bash
mvn clean test
```

### Test Coverage

The following services have **JaCoCo** test coverage reporting enabled:
- **Auth Service**: Full unit and integration tests
- **Order Service**: Comprehensive test suite
- **Payment Gateway Service**: Payment flow testing

Coverage reports are generated in:
- `auth-service/target/site/jacoco/index.html`
- `order-service/target/site/jacoco/index.html`
- `payment-gateway-service/target/site/jacoco/index.html`

### Test Types

1. **Unit Tests:**
   - Service layer tests
   - Controller tests with MockMvc
   - Utility class tests
   - Configuration tests

2. **Integration Tests:**
   - Full Spring Boot context tests
   - Database integration tests (using H2 in-memory database)
   - End-to-end API tests

**Test Reports Location:**
- `target/surefire-reports/` (text and XML reports)
- `target/site/jacoco/` (HTML coverage reports)

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

## Kafka Topics

The application uses the following Kafka topics for event-driven communication:

- **`notification-events`**: User registration, order confirmations, and other email notifications
  - Producers: Auth Service, Order Service
  - Consumers: Notification Service
  - Message Format: JSON with email details (to, subject, body)

## Project Structure

```
EcommerceApplication/
├── auth-service/                    # Authentication & Authorization Service
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/ecommerce/auth/
│   │   │   │   ├── config/        # Security, Kafka configs
│   │   │   │   ├── controller/    # REST endpoints
│   │   │   │   ├── dto/           # Data Transfer Objects
│   │   │   │   ├── entity/        # JPA entities
│   │   │   │   ├── exception/     # Custom exceptions
│   │   │   │   ├── repository/    # Data access layer
│   │   │   │   ├── service/       # Business logic
│   │   │   │   └── util/          # JWT utilities
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                  # Unit & Integration tests
│   ├── pom.xml
│   └── target/
│       └── site/jacoco/          # Test coverage reports
├── catalog-service/               # Product Catalog Service
│   ├── src/
│   │   ├── main/java/com/ecommerce/catalog/
│   │   │   ├── controller/       # Product & Category APIs
│   │   │   ├── dto/              # DTOs
│   │   │   ├── entity/           # Product, Category entities
│   │   │   ├── repository/       # JPA repositories
│   │   │   └── service/          # Business logic with caching
│   │   └── test/
│   └── pom.xml
├── cart-service/                  # Shopping Cart Service
│   ├── src/main/java/com/ecommerce/cart/
│   └── pom.xml
├── order-service/                 # Order Management Service
│   ├── src/main/java/com/ecommerce/order/
│   ├── pom.xml
│   └── target/site/jacoco/       # Test coverage reports
├── payment-gateway-service/       # Payment Processing Service
│   ├── src/main/java/com/ecommerce/payment/
│   ├── pom.xml
│   └── target/site/jacoco/       # Test coverage reports
├── notification-service/          # Email Notification Service
│   ├── src/main/java/com/ecommerce/notification/
│   └── pom.xml
├── discovery-service/             # Eureka Service Registry
│   ├── src/main/java/com/ecommerce/discovery/
│   └── pom.xml
├── specs/                         # Project documentation
│   └── ECOMMERCE_APPLICATION_GUIDE.md
├── docker-compose.yml             # Infrastructure setup
├── init-db.sql                    # Database initialization
├── pom.xml                        # Parent POM
├── .gitignore
└── README.md
```

## Features Implemented

### Core Features
✅ User Authentication & Authorization (JWT + OAuth2)  
✅ Service Discovery & Registration (Eureka)  
✅ Product Catalog Management (CRUD)  
✅ Category Management (CRUD)  
✅ Advanced Product Search with Filters  
✅ Shopping Cart Management  
✅ Order Management with Multiple Items  
✅ Order Status Tracking (6 States)  
✅ Payment Gateway Integration (Razorpay & Stripe)  
✅ Payment Transaction Persistence  
✅ Email Notifications (Async via Kafka)  

### Technical Features
✅ RESTful API Design  
✅ DTO Pattern for API contracts  
✅ Global Exception Handling  
✅ Redis Caching (Products, Categories, Cart)  
✅ Kafka Event-Driven Architecture  
✅ Docker Compose Infrastructure  
✅ Database Persistence (MySQL + JPA/Hibernate)  
✅ Comprehensive Testing (Unit + Integration)  
✅ Code Coverage Reporting (JaCoCo)  
✅ Pagination & Sorting  
✅ Input Validation  
✅ Lombok for boilerplate reduction  
✅ Spring DevTools for rapid development  

## Additional Documentation

- **Detailed Guide**: See `specs/ECOMMERCE_APPLICATION_GUIDE.md` for comprehensive architecture documentation
- **Test Reports**: Available in `target/surefire-reports/` after running tests
- **Coverage Reports**: Available in `target/site/jacoco/` for configured services

## Known Issues

1. **Port Conflict**: Notification Service is configured to use port 8083, which conflicts with Catalog Service. Consider changing notification-service to port 8082.
2. **Email Configuration**: Notification Service requires valid SMTP credentials to be configured before use.
3. **Payment Gateway Keys**: Razorpay and Stripe API keys need to be added to payment-gateway-service configuration.

## Future Enhancements

- [ ] API Gateway implementation (Spring Cloud Gateway)
- [ ] Distributed tracing (Spring Cloud Sleuth + Zipkin)
- [ ] Centralized configuration (Spring Cloud Config)
- [ ] Circuit breaker pattern (Resilience4j)
- [ ] Docker images for each service
- [ ] Kubernetes deployment manifests
- [ ] Monitoring & metrics (Prometheus + Grafana)
- [ ] API documentation (Swagger/OpenAPI)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Troubleshooting

### Port Already in Use
If a port is already in use, you can change it in the respective service's `application.properties` file. Note that Notification Service currently conflicts with Catalog Service on port 8083.

### Database Connection Issues
- Ensure MySQL is running on port 3306
- Verify all databases are created (use `init-db.sql` script)
- Check credentials in each service's `application.properties`

### Kafka Connection Issues
- Ensure Zookeeper is running on port 2181
- Ensure Kafka is running on port 9092
- Default bootstrap server: `localhost:9092`

### Redis Connection Issues
- Ensure Redis is running on port 6379
- Default host: `localhost`

### Service Registration Issues
- Start Discovery Service first before other services
- Verify Eureka dashboard at http://localhost:8761
- Check `eureka.client.service-url.defaultZone` in application.properties

## License

This project is developed for educational and demonstration purposes.

## Support

For issues and questions:
- Check the documentation in `specs/ECOMMERCE_APPLICATION_GUIDE.md`
- Review test cases for usage examples
- Examine the Eureka dashboard for service health at http://localhost:8761
- Check service logs for detailed error messages

---

**Built with ❤️ using Spring Boot and Microservices Architecture**


