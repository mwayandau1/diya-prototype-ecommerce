
# E-Commerce Backend Application

This is a robust e-commerce backend application built with Spring Boot and Spring Security, featuring custom JWT authentication with access and refresh token mechanisms.

## Features

- **User Authentication**
    - JWT-based authentication with access and refresh tokens
    - User registration and login
    - Role-based authorization

- **Product Management**
    - Create, read, update, and delete products
    - Product categorization
    - Product search
    - Filter products by category

- **Shopping Cart**
    - Add items to cart
    - Update cart items
    - Remove items from cart
    - View cart contents

- **Order Processing**
    - Create orders
    - Order history
    - Order status tracking
    - Order cancellation

- **Payment Handling**
    - Process payments
    - Handle refunds

## Technical Stack

- **Spring Boot**: Core framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **H2 Database**: In-memory database for development
- **JWT**: JSON Web Token for stateless authentication
- **Lombok**: Reduce boilerplate code
- **Jakarta Validation**: Data validation

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.6+ or Gradle 7.0+

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/your-username/ecommerce-backend.git
   cd ecommerce-backend
   ```

2. Build the project:
   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and obtain tokens
- `POST /api/auth/refresh-token` - Refresh access token
- `POST /api/auth/logout` - Logout and invalidate refresh token

### Products

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{categoryId}` - Get products by category
- `GET /api/products/search?keyword={keyword}` - Search products
- `POST /api/products` - Create a new product (Admin)
- `PUT /api/products/{id}` - Update a product (Admin)
- `DELETE /api/products/{id}` - Delete a product (Admin)

### Categories

- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories` - Create a new category (Admin)
- `PUT /api/categories/{id}` - Update a category (Admin)
- `DELETE /api/categories/{id}` - Delete a category (Admin)

### Cart

- `GET /api/cart` - Get user's cart
- `POST /api/cart` - Add item to cart
- `PUT /api/cart/items/{productId}` - Update cart item quantity
- `DELETE /api/cart/items/{productId}` - Remove item from cart
- `DELETE /api/cart` - Clear the cart

### Orders

- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID
- `POST /api/orders` - Create a new order
- `PUT /api/orders/{id}/status` - Update order status (Admin)
- `POST /api/orders/{id}/cancel` - Cancel an order

## Security Configuration

The application is secured using JWT-based authentication. Most endpoints require authentication, except for:
- Authentication endpoints (`/api/auth/**`)
- Product listing and search endpoints

## Database Configuration

The application uses an H2 in-memory database by default. The configuration can be found in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/your_database_name
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

For production, it is recommended to configure a persistent database like PostgreSQL or MySQL.

## Token Configuration

JWT token configuration can be customized in `application.yml`:

```yaml
jwt:
  access-token:
    secret: your-access-token-secret
    expiration-time: 3600000  # 1 hour
  refresh-token:
    secret: your-refresh-token-secret
    expiration-time: 604800000  # 7 days
```

## Development

### Adding a New Entity

1. Create a model class in `com.ecommerce.model`
2. Create a repository interface in `com.ecommerce.repository`
3. Create DTO classes in `com.ecommerce.dto`
4. Create a service class in `com.ecommerce.service`
5. Create a controller class in `com.ecommerce.controller`

### Business Logic

The core business logic is implemented in service classes, following the separation of concerns principle.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
