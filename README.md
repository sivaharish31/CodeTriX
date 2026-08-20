# CodeTriX - College Coding Event Platform

## Authentication & Authorization Module

This module provides secure authentication and authorization for the CodeTriX platform.

### Features

- JWT-based authentication
- Role-based access control (ADMIN, TEAM)
- BCrypt password hashing
- Token blacklisting for logout
- Validation on all requests
- Comprehensive error handling

### Tech Stack

- Java 21
- Spring Boot 3.3.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt 0.12.5)

### API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/admin/login` | Admin login | Public |
| POST | `/api/auth/team/login` | Team login | Public |
| POST | `/api/auth/logout` | Logout (invalidate token) | Authenticated |
| GET | `/api/auth/me` | Get current user info | Authenticated |

### Getting Started

#### Prerequisites

- Java 21
- PostgreSQL 15+
- Maven 3.9+

#### Database Setup

```sql
CREATE DATABASE codetrix;
```

#### Configuration

Set environment variables or update `application.yml`:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-base64-encoded-secret-key
```

#### Run the Application

```bash
mvn spring-boot:run
```

### Default Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`

**Sample Team:**
- Team ID: `TEAM001`
- PIN: `1234`

> **Warning:** Change default credentials in production!

### API Usage Examples

#### Admin Login

```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

#### Team Login

```bash
curl -X POST http://localhost:8080/api/auth/team/login \
  -H "Content-Type: application/json" \
  -d '{"teamId": "TEAM001", "loginPin": "1234"}'
```

#### Get Current User

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <your-jwt-token>"
```

#### Logout

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Security

- Passwords are hashed using BCrypt (strength 12)
- JWT tokens expire after 24 hours
- Blacklisted tokens are cleaned up hourly
- CSRF disabled (stateless JWT auth)
- Role-based endpoint protection

### Project Structure

```
src/main/java/com/codetrix/
├── CodeTrixApplication.java
├── auth/
│   ├── config/
│   │   ├── DataInitializer.java
│   │   ├── JwtConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── AdminLoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── ErrorResponse.java
│   │   ├── MessageResponse.java
│   │   ├── TeamLoginRequest.java
│   │   └── UserInfoResponse.java
│   ├── entity/
│   │   ├── Role.java
│   │   ├── Team.java
│   │   └── User.java
│   ├── exception/
│   │   ├── AuthException.java
│   │   └── GlobalExceptionHandler.java
│   ├── repository/
│   │   ├── RoleRepository.java
│   │   ├── TeamRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── CustomAccessDeniedHandler.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   └── JwtAuthenticationFilter.java
│   └── service/
│       ├── AuthService.java
│       ├── CustomUserDetailsService.java
│       ├── JwtService.java
│       └── TokenBlacklistService.java
└── common/
    └── enums/
        └── RoleType.java
```
