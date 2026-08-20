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

### 3. Event & Server-Controlled Timer Module

Server-authoritative timer system for the coding competition. **Never trusts the browser.**

#### Event Structure
| Round | Type | Duration |
|-------|------|----------|
| 1 | CODING | 15 minutes |
| 2 | DEBUGGING | 15 minutes |
| 3 | CTF | 15 minutes |
| **Total** | | **45 minutes** |

#### Features
- **Server-controlled timer** - Browser only displays, never controls
- **One-time start** - Event can only be started once, cannot be paused/stopped/extended
- **Automatic round transitions** - Rounds transition automatically at exact times
- **Submission deadline enforcement** - Backend rejects late submissions
- **Browser-refresh resistant** - Timer continues correctly on reconnect
- **WebSocket real-time updates** - Broadcasts timer ticks and round changes
- **Clock-manipulation proof** - Client system clock cannot affect server time

#### Event Status Lifecycle
```
NOT_STARTED → RUNNING → COMPLETED
```

#### Round Status Lifecycle
```
LOCKED → RUNNING → COMPLETED
```

#### Event API Endpoints

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/admin/event/start` | Start the event (once only) | Admin |
| GET | `/api/event/status` | Get full event status | Authenticated |
| GET | `/api/event/time` | Get current time info | Authenticated |
| GET | `/api/event/current-round` | Get active round details | Authenticated |

#### WebSocket Endpoints

| Endpoint | Topics | Description |
|----------|--------|-------------|
| `/ws/event` | `/topic/timer` | Timer ticks (every second) |
| | `/topic/round` | Round change notifications |
| | `/topic/event` | Event start/end notifications |

#### WebSocket Message Types
- `TICK` - Regular timer update
- `ROUND_CHANGE` - Round transition notification
- `EVENT_START` - Event started
- `EVENT_END` - Event completed

---

## Tech Stack

- Java 21
- Spring Boot 3.3.0
- Spring Security
- Spring Data JPA
- Spring WebSocket
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

### Start Event (Admin Only)

```bash
curl -X POST http://localhost:8080/api/admin/event/start \
  -H "Authorization: Bearer <admin-token>"
```

Response:
```json
{
  "eventId": 1,
  "eventName": "CodeTriX Competition",
  "status": "RUNNING",
  "startTime": "2024-01-15T10:00:00Z",
  "endTime": "2024-01-15T10:45:00Z",
  "serverTime": "2024-01-15T10:00:00Z",
  "totalDurationSeconds": 2700,
  "currentRoundNumber": 1,
  "currentRoundType": "CODING",
  "message": "Event started successfully. Timer is now running."
}
```

### Get Event Time

```bash
curl -X GET http://localhost:8080/api/event/time \
  -H "Authorization: Bearer <token>"
```

Response:
```json
{
  "serverTime": "2024-01-15T10:05:30Z",
  "eventStatus": "RUNNING",
  "eventRemainingSeconds": 2370,
  "currentRoundNumber": 1,
  "currentRoundType": "CODING",
  "currentRoundStatus": "RUNNING",
  "roundRemainingSeconds": 570,
  "roundEndTime": "2024-01-15T10:15:00Z"
}
```

### WebSocket Connection (JavaScript)

```javascript
const socket = new SockJS('/ws/event');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to timer updates
    stompClient.subscribe('/topic/timer', function(message) {
        const data = JSON.parse(message.body);
        console.log('Timer:', data.roundRemainingSeconds, 'seconds');
        updateTimerDisplay(data);
    });

    // Subscribe to round changes
    stompClient.subscribe('/topic/round', function(message) {
        const data = JSON.parse(message.body);
        console.log('Round changed:', data.currentRoundType);
        handleRoundChange(data);
    });
});
```

### Create Team

```bash
curl -X POST http://localhost:8080/api/admin/teams \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{"teamName": "Code Warriors"}'
```

### Team Login

```bash
curl -X POST http://localhost:8080/api/auth/team/login \
  -H "Content-Type: application/json" \
  -d '{"teamId": "CTX042", "loginPin": "847291"}'
```

## Project Structure

```
src/main/java/com/codetrix/
├── CodeTrixApplication.java
├── auth/                           # Authentication Module
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── security/
│   └── service/
├── team/                           # Team Management Module
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   └── service/
├── event/                          # Event & Timer Module
│   ├── config/
│   │   ├── WebSocketConfig.java
│   │   └── SchedulingConfig.java
│   ├── controller/
│   │   └── EventController.java
│   ├── dto/
│   │   ├── EventStatusResponse.java
│   │   ├── RoundStatusResponse.java
│   │   ├── TimeResponse.java
│   │   ├── CurrentRoundResponse.java
│   │   ├── EventStartResponse.java
│   │   └── TimerBroadcast.java
│   ├── entity/
│   │   ├── Event.java
│   │   ├── Round.java
│   │   ├── EventStatus.java
│   │   ├── RoundStatus.java
│   │   └── RoundType.java
│   ├── exception/
│   ├── repository/
│   ├── service/
│   │   └── EventService.java
│   └── websocket/
│       └── EventWebSocketService.java
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
- `events` - Event configuration and state
- `rounds` - Round configuration and state

## Security Notes

- Passwords/PINs are hashed using BCrypt (strength 12)
- JWT tokens expire after 24 hours
- Blacklisted tokens are cleaned up hourly
- CSRF disabled (stateless JWT auth)
- Role-based endpoint protection
- **Server time is the only source of truth**
- **Submissions rejected after round end time**
- **Timer cannot be modified after event starts**
