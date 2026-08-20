# CodeTriX - College Coding Event Platform

## Modules

### 1. Authentication & Authorization Module

Provides secure JWT-based authentication and role-based access control.

#### Features
- JWT-based authentication with token blacklisting
- Role-based access control (ADMIN, TEAM)
- BCrypt password hashing (strength 12)
- Comprehensive error handling

#### Auth API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/auth/admin/login` | Admin login | Public |
| POST | `/api/auth/team/login` | Team login | Public |
| POST | `/api/auth/logout` | Logout (invalidate token) | Authenticated |
| GET | `/api/auth/me` | Get current user info | Authenticated |

---

### 2. Team Registration & Management Module

Manages team registration and team member enrollment for the coding event.

#### Features
- Maximum 27 teams limit enforcement
- Auto-generated unique team codes (CTXxxx format)
- Secure 6-digit PIN generation
- Team member management with roll number uniqueness
- Status-based team lifecycle (REGISTERED, ACTIVE, DISQUALIFIED, COMPLETED)
- Modification restrictions after event starts

#### Team Management API Endpoints (Admin Only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/admin/teams` | Create a new team |
| GET | `/api/admin/teams` | List all teams with members |
| GET | `/api/admin/teams/{teamId}` | Get team details |
| PUT | `/api/admin/teams/{teamId}` | Update team name/status |
| POST | `/api/admin/teams/{teamId}/credentials` | Regenerate login PIN |
| POST | `/api/admin/teams/{teamId}/members` | Add team member |
| PUT | `/api/admin/teams/{teamId}/members/{memberId}` | Update member |
| DELETE | `/api/admin/teams/{teamId}/members/{memberId}` | Remove member |

---

## Tech Stack

- Java 21
- Spring Boot 3.3.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt 0.12.5)

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL 15+
- Maven 3.9+

### Database Setup

```sql
CREATE DATABASE codetrix;
```

### Configuration

Set environment variables or update `application.yml`:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-base64-encoded-secret-key
```

### Run the Application

```bash
mvn spring-boot:run
```

## Default Credentials

**Admin:**
- Username: `admin`
- Password: `admin123`

> **Warning:** Change default credentials in production!

## API Usage Examples

### Admin Login

```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

### Create Team

```bash
curl -X POST http://localhost:8080/api/admin/teams \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{"teamName": "Code Warriors"}'
```

Response:
```json
{
  "teamId": 1,
  "teamCode": "CTX042",
  "teamName": "Code Warriors",
  "loginPin": "847291",
  "message": "Please share these credentials securely with the team. The PIN cannot be retrieved again."
}
```

### Add Team Member

```bash
curl -X POST http://localhost:8080/api/admin/teams/1/members \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "name": "John Doe",
    "rollNumber": "CS2021001",
    "college": "ABC Engineering College",
    "email": "john@example.com"
  }'
```

### Team Login

```bash
curl -X POST http://localhost:8080/api/auth/team/login \
  -H "Content-Type: application/json" \
  -d '{"teamId": "CTX042", "loginPin": "847291"}'
```

### List All Teams

```bash
curl -X GET http://localhost:8080/api/admin/teams \
  -H "Authorization: Bearer <admin-token>"
```

Response:
```json
{
  "teams": [...],
  "totalTeams": 5,
  "maxTeams": 27,
  "availableSlots": 22
}
```

## Project Structure

```
src/main/java/com/codetrix/
├── CodeTrixApplication.java
├── auth/                           # Authentication Module
│   ├── config/
│   │   ├── DataInitializer.java
│   │   ├── JwtConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   ├── entity/
│   │   ├── Role.java
│   │   └── User.java
│   ├── exception/
│   ├── repository/
│   ├── security/
│   └── service/
├── team/                           # Team Management Module
│   ├── controller/
│   │   └── TeamManagementController.java
│   ├── dto/
│   │   ├── CreateTeamRequest.java
│   │   ├── UpdateTeamRequest.java
│   │   ├── AddMemberRequest.java
│   │   ├── UpdateMemberRequest.java
│   │   ├── TeamResponse.java
│   │   ├── MemberResponse.java
│   │   ├── TeamCredentialsResponse.java
│   │   └── TeamListResponse.java
│   ├── entity/
│   │   ├── Team.java
│   │   ├── TeamMember.java
│   │   └── TeamStatus.java
│   ├── exception/
│   ├── repository/
│   └── service/
│       └── TeamService.java
└── common/
    └── enums/
        └── RoleType.java
```

## Database Schema

### Tables
- `roles` - System roles (ADMIN, TEAM)
- `users` - Admin users
- `teams` - Registered teams
- `team_members` - Team participants

## Security Notes

- Passwords/PINs are hashed using BCrypt (strength 12)
- JWT tokens expire after 24 hours
- Blacklisted tokens are cleaned up hourly
- CSRF disabled (stateless JWT auth)
- Role-based endpoint protection
- Team modifications blocked after event starts
