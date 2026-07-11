# ToDoList

A full-stack Todo List application with a Spring Boot REST API backend and an Angular web frontend. The app supports user registration/login with JWT-based authentication and full CRUD management of tasks.

## Project Structure

This repository contains two independent projects:

```
todolist-web-rest/
├── src/                    # Backend - Java Spring Boot REST API
│   └── main/java/org/todolist/
│       ├── config/         # Security & CORS configuration
│       ├── controller/     # REST controllers (Auth, ToDoList)
│       ├── dto/            # Request/response DTOs
│       ├── exception/      # Global & custom exception handling
│       ├── repository/     # Data access (repositories, DAOs, entities)
│       ├── security/       # JWT authentication filter
│       ├── service/        # Business logic & user details services
│       └── utill/          # Utility classes (JWT, Excel export, etc.)
├── todo-ui/                # Frontend - Angular application
│   └── src/app/
│       ├── core/           # Guards, interceptors, core services
│       ├── features/
│       │   ├── auth/       # Login component
│       │   └── tasks/      # Task list, task form, task page
│       └── model/          # TypeScript models & enums
└── pom.xml                 # Maven build file for the backend
```

## Tech Stack

**Backend**
- Java, Spring Boot
- Spring Security with JWT authentication
- Maven

**Frontend**
- Angular
- TypeScript
- SCSS

## Features

- User registration and login secured with JWT
- Create, view, update, and delete tasks
- Task status and priority management
- Route guards and HTTP interceptors on the frontend for authenticated requests
- CORS-configured REST API for frontend-backend communication

## Prerequisites

- Java 17+ (or the version configured in `pom.xml`)
- Maven 3.6+
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Gourabh72/todolist.git
cd todolist
```

### 2. Run the backend

```bash
mvn clean install
mvn spring-boot:run
```

The API will start on `http://localhost:8080` (default; check `application.yml`/`application.properties` for the configured port).

### 3. Run the frontend

```bash
cd todo-ui
npm install
ng serve
```

The Angular app will be available at `http://localhost:4200`.

## API Overview

| Endpoint              | Description                     |
|------------------------|----------------------------------|
| `POST /auth/register`  | Register a new user             |
| `POST /auth/login`     | Authenticate and receive a JWT  |
| `GET /tasks`           | Get all tasks for the user       |
| `POST /tasks`          | Create a new task                |
| `PUT /tasks/{id}`      | Update an existing task          |
| `DELETE /tasks/{id}`   | Delete a task                    |

*(Update this table to match your actual controller endpoints and request/response formats.)*

## Configuration

Backend configuration (database connection, JWT secret, CORS origins, etc.) is managed in the Spring Boot application properties/YAML file. Update these values to match your local environment before running the project.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add some feature"`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## License

This project currently has no license specified.
