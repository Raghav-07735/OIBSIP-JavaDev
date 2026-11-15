# Digital Library Management System

Web application built with Java 17, Spring Boot, Thymeleaf, JDBC and SQLite.

## Features
**Admin:** login, add/edit/delete books, view issue history and due dates, manage member accounts, mark fines paid, view reservations and contact messages.

**User:** register/login, browse by category, search by title/author, issue books for 14 days, return books, automatic overdue fine (₹5/day), reserve unavailable titles, view account activity, submit contact queries.

## Requirements
- JDK 17+
- Maven 3.8+
- Internet for the first Maven build to download dependencies

## Run
1. Extract the ZIP and open this folder in VS Code.
2. Open Terminal in the project root (the folder containing `pom.xml`).
3. Run `mvn spring-boot:run`.
4. Open `http://localhost:8080`.

Build a runnable JAR:
```bash
mvn clean package
java -jar target/digital-library-1.0.0.jar
```

## Demo Admin
- Email: `admin@library.com`
- Password: `Admin@123`

Register a regular user from the Register page to test user features. Sample books are seeded automatically. SQLite database file `library.db` is created in the project root.

## Database tables
`users`, `books`, `loans`, `fines`, `reservations`, `messages` (schema in `src/main/resources/schema.sql`).

Passwords are stored as BCrypt hashes. This is an educational starter project; before production use, add CSRF protection, stronger validation, transaction handling, audit logs, email notifications and secure deployment configuration.
