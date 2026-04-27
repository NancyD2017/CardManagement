# Credit Card Management System

A secure **REST API** for managing credit cards with JWT-based authentication, role-based access control, and full Docker support.

---

## Screenshots

<!-- Screenshot: Swagger UI page at http://localhost:8080/swagger-ui.html showing all available endpoints (Auth, Card, User controllers) -->
![Swagger UI](docs/swagger-ui.png)

<!-- Screenshot: Example of a successful JWT token response from POST /api/auth/login — show the JSON body with "token" field -->
![JWT Auth Response](docs/auth-response.png)

<!-- Screenshot: Card list response from GET /api/cards — show paginated JSON response with card objects (masked card numbers) -->
![Card List](docs/card-list.png)

---

## Features

- **JWT Authentication** — secure login and token-based session management
- **Role-based access control** — USER and ADMIN roles with different permissions
- **Card CRUD** — create, view, update, and delete credit cards
- **Card number masking** — card numbers stored and returned in masked format
- **Swagger UI** — interactive API documentation at `/swagger-ui.html`
- **Dockerized** — one-command startup via Docker Compose

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot, Spring Security |
| Auth | JWT (JSON Web Tokens) |
| Database | MySQL |
| Infrastructure | Docker, Docker Compose |
| Docs | Swagger / OpenAPI |
| Build | Gradle |

---

## Getting Started

### Prerequisites
- Docker 20.10+
- Docker Compose 2.20+

### Run with Docker Compose

1. **Clone the repository**
   ```bash
   git clone https://github.com/NancyD2017/CardManagement.git
   cd CardManagement
   ```

2. **Start all services**
   ```bash
   docker-compose up --build
   ```

3. Services available:
   - Application: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Configuration

Key environment variables (configurable in `docker-compose.yml`):

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/credit_card_management
  SPRING_DATASOURCE_USERNAME: user
  SPRING_DATASOURCE_PASSWORD: password
  JWT_SECRET: your-secret-key
```

---

## API Reference

### Authentication
```http
POST /api/auth/register    — Register a new user
POST /api/auth/login       — Login and receive JWT token
```

### Cards (requires Authorization: Bearer <token>)
```http
GET    /api/cards          — List all cards (paginated)
POST   /api/cards          — Create a new card
GET    /api/cards/{id}     — Get card by ID
PUT    /api/cards/{id}     — Update card
DELETE /api/cards/{id}     — Delete card (ADMIN only)
```

---

##  License

Test assignment project for Java Developer Junior position.
