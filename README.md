# Hotel Management Web Application

This project contains:
- `backend`: Spring Boot API secured with JWT filter
- `frontend`: React app for authentication and hotel CRUD

## Backend (Spring Boot)

Requirements:
- Java 17+
- Maven 3.9+

Run:
```bash
cd backend
mvn spring-boot:run
```

Backend URL:
- `http://localhost:8080`

Key endpoints:
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/hotels` (JWT required)
- `POST /api/hotels` (JWT required)
- `PUT /api/hotels/{id}` (JWT required)
- `DELETE /api/hotels/{id}` (JWT required)

## Frontend (React + Vite)

Requirements:
- Node.js 18+
- npm 9+

Run:
```bash
cd frontend
npm install
npm run dev
```

Frontend URL:
- `http://localhost:5173`

## Notes

- JWT is read from `Authorization: Bearer <token>` by `JwtAuthenticationFilter`.
- Backend uses H2 in-memory DB (`jdbc:h2:mem:hotel_db`).
