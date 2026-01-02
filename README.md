# Todo Fullstack (Spring Boot + React) — JWT Auth

A fullstack Todo application with:
- Backend: Spring Boot (REST API), MySQL, Spring Security + JWT
- Frontend: React (Vite), Axios, React Router
- Features: Register/Login, Create/Read/Update/Delete Todos, protected routes

---

## Tech Stack

**Backend**
- Java 17
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA (Hibernate)
- MySQL

**Frontend**
- React (Vite)
- React Router
- Axios

---

## Features

- User Registration + Login (JWT token returned)
- JWT stored client-side and sent via `Authorization: Bearer <token>`
- Protected API endpoints (requires JWT)
- Todo CRUD:
  - Create todo
  - List todos
  - Update todo (toggle completed)
  - Delete todo
- Todos are user-scoped (different users do not see each other’s todos)

## Project Structure
todo-fullstack-jwt/
backend/ # Spring Boot API
frontend/ # React UI (Vite)



## Prerequisites

- Java 17 installed
- Maven wrapper included (`./mvnw`)
- Node.js + npm installed
- MySQL running locally


## Backend Setup (Spring Boot)

### 1) Create database
Create a MySQL database
```sql
CREATE DATABASE todo_app;

