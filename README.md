# todo-fullstack-jwt

Full-stack Todo application:
- **Backend:** Spring Boot (JWT Auth, MySQL, REST API)
- **Frontend:** React (Vite) UI

## Features

### Core
- User authentication (JWT)
- Todo CRUD (create/list/update/delete)
- Per-user data isolation (each user only sees their own todos)

### Bonus
- Search / Filter / Sort todos:
  - `GET /api/todos?q=keyword`
  - `GET /api/todos?completed=true|false`
  - `GET /api/todos?sort=id|title|completed&dir=asc|desc`
- Request logging (method/path/status/duration)
- Image upload (per user):
  - Upload `POST /api/files` (multipart)
  - List `GET /api/files`
  - View/Download `GET /api/files/{id}`
  - Delete `DELETE /api/files/{id}`

---

## Tech Stack
- Java 17, Spring Boot
- Spring Security + JWT
- MySQL
- React + Vite
- Axios

---

## Project Structure

todo-fullstack-jwt/
backend/ Spring Boot API
frontend/ React (Vite) UI


---

## Backend Setup (Spring Boot)

### 1) Create local config (not committed)

This project uses a local profile file which must NOT be pushed to Git:

`backend/src/main/resources/application-local.properties`

You can copy from the example file if provided:
`backend/src/main/resources/application-local.properties.example`

Example:

```properties
# DB (LOCAL)
spring.datasource.url=jdbc:mysql://localhost:3306/todo_app
spring.datasource.username=todo_user
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT (LOCAL)
app.jwt.secret=CHANGE_ME_TO_A_LONG_RANDOM_SECRET_AT_LEAST_32_CHARS
app.jwt.expiration-ms=86400000

# Optional
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.open-in-view=false
