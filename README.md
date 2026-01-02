# Todo Full-Stack (Spring Boot + React) — JWT Auth

A full-stack Todo application built for a technical assignment.

- Backend: Java Spring Boot + REST API + MySQL
- Frontend: React (Vite) — separated project folder
- Auth: Register/Login with JWT (Bearer token)
- Features: User-specific CRUD for Todos (users do not see each other’s todos)

## Assignment Requirements Coverage

This project meets the “must-have” requirements:
- Backend (Spring Boot): Register/Login, CRUD, Database (SQL), REST API:contentReference[oaicite:2]{index=2}
- Frontend separated from backend, using React framework:contentReference[oaicite:3]{index=3}
- Git version control with clear commit history:contentReference[oaicite:4]{index=4}
Bonus implemented: JWT Authentication:contentReference[oaicite:5]{index=5}

---

## Tech Stack

### Backend
- Java 17, Spring Boot
- Spring Security (JWT)
- Spring Data JPA + MySQL

### Frontend
- React (Vite)
- Axios
- React Router DOM

---

## Project Structure

todo-fullstack-jwt/
backend/ # Spring Boot API
frontend/ # React (Vite) UI




---

## Configuration (IMPORTANT)

### Backend local config (not committed)
This project uses a local profile file that must NOT be pushed to Git:

`backend/src/main/resources/application-local.properties`

Create it (or edit your existing one) with your own values:

```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/todo_app
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JPA
spring.jpa.hibernate.ddl-auto=update

# JWT (HS256 needs 32+ chars secret)
app.jwt.secret=CHANGE_ME_TO_A_LONG_RANDOM_SECRET_AT_LEAST_32_CHARS
app.jwt.expiration-ms=86400000

# Profile
spring.profiles.active=local
