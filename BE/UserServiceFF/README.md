# User Service

The **User Service** is a microservice responsible for managing users in the FimeForms system.
It handles **registration, login, user details, updates, role management, and validation.**

## Features
- **User Registration**
    - Email, first name, and last name validation
    - Password hashing with **HMAC-SHA256**
    - Duplicate email check
- **User Authentication**
    - Login via email and password
    - Secure password comparison via hashing
- **User Retrieval**
    - Get user by `id`
    - Get user by `email`
    - Get all users
- **User Data Updates**
    - Update email (with validation and uniqueness check)
    - Update first name and last name (with length and format validation)
    - Change password
- **Role Management**
    - Update user role (`USER` ⇄ `ADMIN`)
- **Global Exception Handling**
    - Specific exceptions for missing or invalid data
    - Proper HTTP status codes with detailed error messages

## Technologies
- **Java 17**
- **Spring Boot**
- **Maven**
- **Spring Data JPA** (PostgreSQL)
- **HikariCP** (connection pooling)
- **MapStruct** (DTO mapping)
- **Dotenv** (environment variable loading)
- **Lombok** (boilerplate reduction)

## Environment Variables
The service requires the following environment variables in a `.env` file:

```env
USER_DB_URL=jdbc:postgresql://POSTGRES_HOST_URL
USER_DB_USERNAME=postgres
USER_DB_PASSWORD=yourpassword
HMAC_SECRET=your-secret-key
```

## Running the Service

Clone the repository:
```git
git clone https://github.com/your-repo/user-service.git
cd user-service
```

Configure your .env file in the root directory.

Start the application:
```
mvn spring-boot:run
```

By default, the service runs on:

`http://localhost:8081`

## API Endpoints 

### Authentication

Register User
```
POST /users/register
```

Request Body

```
{
    "email": "john@example.com",
    "password": "mypassword",
    "firstName": "John",
    "lastName": "Doe"
}
```


Responses

**201 Created** – User successfully registered

**400 Bad Request** – Invalid input (e.g., email already exists)