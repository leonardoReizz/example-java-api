# API Server Example

A small REST API built with **Spring Boot** to manage a list of "servers" (hosts, with IP, port and credentials). This is a **learning project** — I built it to get familiar with Java and the Spring ecosystem, and I keep it documented here as a reference example for later.

It implements a clean CRUD with layered architecture (Controller → Service → Repository), DTOs, bean validation with validation groups, and centralized exception handling.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.6**
  - `spring-boot-starter-web` — REST endpoints
  - `spring-boot-starter-data-jpa` — persistence via JPA/Hibernate
  - `spring-boot-starter-validation` — field validation (`@NotBlank`, `@Email`, etc.)
- **H2** — in-memory database (great for practice; data is lost on restart)
- **Maven** — build tool (wrapper included via `mvnw` / `mvnw.cmd`)

## Project Structure

```
src/main/java/com/example/api/
├── ApiApplication.java              # Application entry point
├── controller/
│   └── ServerController.java        # REST endpoints under /servers
├── service/
│   └── ServerService.java           # Business logic
├── repository/
│   └── ServerRepository.java        # JPA repository (extends JpaRepository)
├── model/
│   └── Server.java                  # JPA entity mapped to the "servers" table
├── dto/
│   ├── ServerDTO.java               # Input payload + validation rules
│   └── ServerListResponse.java      # Wrapper for list responses
├── validation/
│   └── ValidationGroups.java        # OnCreate / OnUpdate validation groups
└── exception/
    ├── NotFoundException.java        # Thrown when a resource is missing
    └── GlobalExceptionHandler.java   # Maps exceptions to HTTP responses
```

## Running the Project

Requires JDK 21 installed.

```bash
# Start the application (uses the Maven wrapper, no global Maven needed)
./mvnw spring-boot:run

# Run the tests
./mvnw test

# Build a runnable jar
./mvnw clean package
```

The API starts on **http://localhost:8080**.

### H2 Console

The in-memory database has a web console enabled at **http://localhost:8080/h2-console**.

| Field    | Value                     |
|----------|---------------------------|
| JDBC URL | `jdbc:h2:mem:servidoresdb`|
| User     | `sa`                      |
| Password | *(empty)*                 |

## API Endpoints

Base path: `/servers`

| Method   | Path            | Description                  | Body         |
|----------|-----------------|------------------------------|--------------|
| `GET`    | `/servers`      | List all servers             | —            |
| `GET`    | `/servers/{id}` | Get a server by UUID         | —            |
| `POST`   | `/servers`      | Create a server              | `ServerDTO`  |
| `PATCH`  | `/servers/{id}` | Update an existing server    | `ServerDTO`  |
| `DELETE` | `/servers/{id}` | Delete a server by UUID      | —            |

### Request body (`ServerDTO`)

```json
{
  "name": "web-01",
  "ip": "192.168.0.10",
  "port": 22,
  "username": "admin",
  "password": "secret"
}
```

**Validation rules:**

| Field      | Rules                                                        |
|------------|-------------------------------------------------------------|
| `name`     | required (on create), 2–100 characters                      |
| `ip`       | required (on create), must be a valid IPv4 address          |
| `port`     | required (on create), between 1 and 65535                   |
| `username` | required (on create)                                        |
| `password` | required (on create); never returned in responses (`@JsonIgnore`) |

> Validation uses **validation groups** (`ValidationGroups.OnCreate`) so that the
> `@NotBlank` / `@NotNull` checks only apply on creation, while `PATCH` (update)
> can send a partial payload.

### Example requests

```bash
# Create
curl -X POST http://localhost:8080/servers \
  -H "Content-Type: application/json" \
  -d '{"name":"web-01","ip":"192.168.0.10","port":22,"username":"admin","password":"secret"}'

# List
curl http://localhost:8080/servers

# Get by id
curl http://localhost:8080/servers/{id}

# Update
curl -X PATCH http://localhost:8080/servers/{id} \
  -H "Content-Type: application/json" \
  -d '{"name":"web-02"}'

# Delete
curl -X DELETE http://localhost:8080/servers/{id}
```

## Error Handling

`GlobalExceptionHandler` centralizes error responses:

- **400 Bad Request** — validation failures, returned as a map of `field → message`.
- **404 Not Found** — when a server with the given id does not exist.

Example validation error response:

```json
{
  "ip": "Invalid IP",
  "port": "port is required"
}
```

## Concepts Practiced

This project was built to practice and document the following Spring/Java concepts:

- Layered architecture (Controller / Service / Repository)
- Constructor-based dependency injection
- JPA entities with `@Entity`, `@Id`, UUID generation, and timestamps
- Spring Data JPA repositories (CRUD for free via `JpaRepository`)
- DTOs as Java `record`s
- Bean Validation with **validation groups** for create vs. update
- Centralized exception handling with `@RestControllerAdvice`
- Hiding sensitive fields from JSON output (`@JsonIgnore`)

---

## Roadmap

Planned next steps to evolve this from a simple CRUD into an authenticated API.
Documented here as a learning guide for when I revisit the project.

### 1. User Creation

- [ ] Add `spring-boot-starter-security` dependency.
- [ ] Create a `User` entity (`id`, `name`, `email`, `password`, `role`, `createdAt`).
- [ ] Create `UserRepository` (`extends JpaRepository<User, String>`) with a
      `findByEmail` method.
- [ ] Create `UserDTO` / `RegisterRequest` with validation (`@Email`, `@Size` on password).
- [ ] Hash passwords with `BCryptPasswordEncoder` before saving — **never store
      plain-text passwords**.
- [ ] Expose `POST /auth/register` to create a new user.
- [ ] Enforce unique emails (DB constraint + friendly 409 Conflict error).

### 2. Login

- [ ] Create `LoginRequest` DTO (`email`, `password`).
- [ ] Implement `UserDetailsService` to load users by email for Spring Security.
- [ ] Configure an `AuthenticationManager` / `AuthenticationProvider` using the
      `BCryptPasswordEncoder`.
- [ ] Expose `POST /auth/login` that validates credentials and returns a JWT.
- [ ] Return 401 Unauthorized on invalid credentials.

### 3. JWT (JSON Web Token)

- [ ] Add a JWT library (e.g. `io.jsonwebtoken:jjwt` or `com.auth0:java-jwt`).
- [ ] Create a `JwtService` to **generate** tokens (subject = user id/email,
      issued-at, expiration, signature with a secret key) and **validate** them.
- [ ] Store the secret and expiration time in `application.properties`
      (use environment variables for real deployments).
- [ ] Add a `JwtAuthenticationFilter` (`OncePerRequestFilter`) that reads the
      `Authorization: Bearer <token>` header, validates the token, and sets the
      `SecurityContext`.
- [ ] Configure `SecurityFilterChain`:
  - Permit `/auth/**` and the H2 console without authentication.
  - Require authentication for all `/servers/**` endpoints.
  - Make the API **stateless** (`SessionCreationPolicy.STATELESS`).
- [ ] (Optional) Add **roles/authorities** and method-level security
      (`@PreAuthorize`) to restrict certain operations to admins.
- [ ] (Optional) Add **refresh tokens** so access tokens can be short-lived.

### Target authentication flow

```
1. POST /auth/register  → create user (password is hashed)
2. POST /auth/login      → returns { "token": "<jwt>" }
3. Call protected routes with header:
   Authorization: Bearer <jwt>
4. JwtAuthenticationFilter validates the token on every request
   and authorizes access to /servers/**
```

---

> **Note:** This is an educational project. The credentials stored on `Server`
> records and the JWT setup described above are for learning purposes — review
> security best practices (secret management, HTTPS, token rotation) before using
> anything like this in production.
