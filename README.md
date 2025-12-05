# 👤 User Identity Service

> User authentication, registration, and profile management for the CS02 E-Commerce Platform

## 📋 Overview

The User Identity Service is the core authentication and user management service. It handles user registration, login, JWT token management, profile updates, and user-related data including addresses and payment methods.

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Language | Java | 17 |
| Framework | Spring Boot | 4.0.0 |
| Database | PostgreSQL | 15 |
| Security | Spring Security | Latest |
| JWT | JJWT (io.jsonwebtoken) | 0.11.5 |
| Documentation | Springdoc OpenAPI | Latest |
| Build Tool | Maven | 3.x |

## 🚀 API Endpoints

### Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | No | User registration |
| `POST` | `/api/auth/login` | No | User login |
| `POST` | `/api/auth/refresh` | No | Refresh JWT token |
| `POST` | `/api/auth/logout` | Yes | Logout (invalidate token) |

### User Profile

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/users/me` | Yes | Get current user profile |
| `PUT` | `/api/users/me` | Yes | Update profile |
| `PUT` | `/api/users/me/password` | Yes | Change password |
| `GET` | `/api/users` | Admin | Get all users |
| `GET` | `/api/users/{id}` | Admin | Get user by ID |
| `PUT` | `/api/users/{id}/status` | Admin | Enable/disable user |

### Addresses

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/users/me/addresses` | Yes | Get user addresses |
| `POST` | `/api/users/me/addresses` | Yes | Add address |
| `PUT` | `/api/users/me/addresses/{id}` | Yes | Update address |
| `DELETE` | `/api/users/me/addresses/{id}` | Yes | Delete address |
| `PUT` | `/api/users/me/addresses/{id}/default` | Yes | Set default address |

### Payment Methods

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/users/me/payment-methods` | Yes | Get payment methods |
| `POST` | `/api/users/me/payment-methods` | Yes | Add payment method |
| `PUT` | `/api/users/me/payment-methods/{id}` | Yes | Update payment method |
| `DELETE` | `/api/users/me/payment-methods/{id}` | Yes | Delete payment method |
| `PUT` | `/api/users/me/payment-methods/{id}/default` | Yes | Set default payment |

### Recently Viewed

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/api/users/me/recently-viewed` | Yes | Get recently viewed products |
| `POST` | `/api/users/me/recently-viewed` | Yes | Add to recently viewed |
| `DELETE` | `/api/users/me/recently-viewed` | Yes | Clear history |

## 📊 Data Models

### User

```java
{
  "id": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "phone": "+1-555-123-4567",
  "role": "CUSTOMER | ADMIN",
  "loyaltyPoints": 1500,
  "tier": "bronze | silver | gold | platinum",
  "isActive": true,
  "emailVerified": true,
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Address

```java
{
  "id": "uuid",
  "userId": "uuid",
  "name": "Home",
  "street": "123 Main St",
  "city": "San Francisco",
  "state": "CA",
  "zipCode": "94102",
  "country": "USA",
  "phone": "+1-555-123-4567",
  "isDefault": true
}
```

### PaymentMethod

```java
{
  "id": "uuid",
  "userId": "uuid",
  "type": "CREDIT_CARD | DEBIT_CARD | PAYPAL",
  "brand": "visa | mastercard | amex",
  "last4": "4242",
  "expiryMonth": 12,
  "expiryYear": 2025,
  "isDefault": true
}
```

### LoginRequest

```java
{
  "email": "user@example.com",
  "password": "password123"
}
```

### LoginResponse

```java
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "dGhpcyBpcyBhIHJlZnJlc2g...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "name": "John Doe",
    "role": "CUSTOMER"
  }
}
```

## 🔧 Configuration

### Application Properties

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/CSO2_user_identity_service
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: ${JWT_SECRET:YourSuperSecretKeyForJWTTokenGeneration}
  access-token-expiration: 86400000  # 24 hours
  refresh-token-expiration: 604800000  # 7 days

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

### Environment Variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | No | `jdbc:postgresql://localhost:5432/CSO2_user_identity_service` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | No | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | No | `postgres` | Database password |
| `JWT_SECRET` | Yes | - | Secret key for JWT signing |
| `SERVER_PORT` | No | `8081` | Service port |

## 📦 Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
</dependencies>
```

## 🏃 Running the Service

### Local Development

```bash
cd backend/user-identity-service

# Using Maven Wrapper
./mvnw spring-boot:run

# Or with Maven
mvn spring-boot:run
```

### Docker

```bash
cd backend/user-identity-service

# Build JAR
./mvnw clean package -DskipTests

# Build Docker image
docker build -t cs02/user-identity-service .

# Run container
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/CSO2_user_identity_service \
  -e JWT_SECRET=YourSuperSecretKey \
  cs02/user-identity-service
```

## 🗄️ Database Requirements

- **PostgreSQL** running on port `5432`
- Database: `CSO2_user_identity_service`
- Tables auto-created via JPA

### Database Schema

```sql
-- users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    phone VARCHAR(20),
    role VARCHAR(50) DEFAULT 'CUSTOMER',
    loyalty_points INTEGER DEFAULT 0,
    tier VARCHAR(20) DEFAULT 'bronze',
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- addresses table
CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    phone VARCHAR(20),
    is_default BOOLEAN DEFAULT false
);

-- payment_methods table
CREATE TABLE payment_methods (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50),
    brand VARCHAR(50),
    last4 VARCHAR(4),
    expiry_month INTEGER,
    expiry_year INTEGER,
    is_default BOOLEAN DEFAULT false
);

-- refresh_tokens table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- recently_viewed table
CREATE TABLE recently_viewed (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    product_id VARCHAR(100),
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔐 Authentication Flow

### Registration Flow
1. User submits email, password, name
2. Password is hashed using BCrypt
3. User record created with CUSTOMER role
4. JWT tokens generated and returned

### Login Flow
1. User submits email and password
2. Credentials validated against database
3. Access token (24h) and refresh token (7d) generated
4. Tokens returned to client

### Token Refresh Flow
1. Client sends expired access token + valid refresh token
2. Refresh token validated in database
3. New access token generated
4. New refresh token generated (rotation)

### JWT Token Structure
```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "role": "CUSTOMER",
  "iat": 1704067200,
  "exp": 1704153600
}
```

## ✅ Features - Completion Status

| Feature | Status | Notes |
|---------|--------|-------|
| User registration | ✅ Complete | Email + password |
| User login | ✅ Complete | JWT-based |
| Token refresh | ✅ Complete | Token rotation |
| Profile management | ✅ Complete | View and update |
| Password change | ✅ Complete | Secure update |
| Address management | ✅ Complete | Full CRUD |
| Payment methods | ✅ Complete | Full CRUD |
| Default address/payment | ✅ Complete | Set defaults |
| Recently viewed | ✅ Complete | History tracking |
| Role-based access | ✅ Complete | CUSTOMER, ADMIN |
| Admin user listing | ✅ Complete | All users view |
| User status toggle | ✅ Complete | Enable/disable |
| Loyalty points | ✅ Complete | Points tracking |
| Tier system | ✅ Complete | bronze/silver/gold/platinum |

### **Overall Completion: 85%** ✅

## ❌ Not Implemented / Future Enhancements

| Feature | Priority | Notes |
|---------|----------|-------|
| Password reset email | High | Forgot password flow |
| Email verification | Medium | Confirm email address |
| OAuth2/Social login | Medium | Google, GitHub, etc. |
| Two-factor auth (2FA) | Medium | TOTP or SMS |
| Account deletion | Low | GDPR compliance |
| Login history | Low | Security audit |
| Session management | Low | Active sessions view |
| Rate limiting | Medium | Brute force protection |

## 📁 Project Structure

```
user-identity-service/
├── src/
│   ├── main/
│   │   ├── java/com/cs02/identity/
│   │   │   ├── UserIdentityApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── AddressController.java
│   │   │   │   └── PaymentMethodController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Address.java
│   │   │   │   ├── PaymentMethod.java
│   │   │   │   └── RefreshToken.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── AddressRepository.java
│   │   │   │   ├── PaymentMethodRepository.java
│   │   │   │   └── RefreshTokenRepository.java
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── JwtService.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── JwtAuthFilter.java
│   │   │   └── dto/
│   │   │       ├── LoginRequest.java
│   │   │       ├── RegisterRequest.java
│   │   │       └── UserResponse.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
├── Dockerfile
└── README.md
```

## 🧪 Testing

```bash
# Run unit tests
./mvnw test

# Test registration
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123", "name": "Test User"}'

# Test login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "password123"}'

# Test profile (with token)
curl -H "Authorization: Bearer <token>" http://localhost:8081/api/users/me

# Test addresses
curl -H "Authorization: Bearer <token>" http://localhost:8081/api/users/me/addresses

# Swagger UI
open http://localhost:8081/swagger-ui.html
```

## 🔗 Related Services

- [API Gateway](../api-gateway/README.md) - Routes `/api/auth/*` and `/api/users/*`
- [Order Service](../order-service/README.md) - User info for orders
- [Support Service](../support-service/README.md) - User verification
- [Notifications Service](../notifications-service/README.md) - User contact info

## 📝 Notes

- Service runs on port **8081**
- Uses **PostgreSQL** for user data
- JWT tokens are signed with **HS256** algorithm
- Passwords are hashed using **BCrypt**
- Refresh tokens are stored in database for invalidation
- API documentation available at `/swagger-ui.html`
- Default admin user may need to be seeded manually
