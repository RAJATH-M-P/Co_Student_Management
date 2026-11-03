# CampusFlow — Full-stack Student Management System

Your original Spring Boot + MySQL student REST API has been expanded with a React frontend and modules for student records, attendance, marks, fee tracking, announcements, dashboard summaries and CSV reports.

## Stack
- Backend: Java 17, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security, JWT, MySQL 8
- Frontend: React, Vite, Lucide icons, responsive CSS
- API: REST/JSON, bearer-token authentication

## Project layout
- `src/main/java/com/example/demo/` — Spring Boot entities, repositories, authentication and API controllers
- `src/main/resources/application.properties` — backend/database/JWT configuration
- `frontend/` — React app
- `docker-compose.yml` — MySQL and backend containers

## Run locally (recommended for first run)

### 1. Start MySQL
Install MySQL 8 and create a database named `studentdb`, or run only the database service using Docker:

```bash
docker compose up -d mysql-db
```

Default local development settings are `localhost:3306`, database `studentdb`, user `root`, password `root123`. Change these using `DB_URL`, `DB_USERNAME` and `DB_PASSWORD` environment variables for your own setup.

### 2. Start backend
From the project root:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend: `http://localhost:8081`

### 3. Start React frontend
Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## First login
Choose **Create account** on the login screen to register the first administrator. Password must be at least 8 characters. Then sign in. The sample form is prefilled with username `admin` and password `admin1234`; these are only convenient form defaults and do not represent an existing account. Create the account before signing in.

## API routes
All `/api/**` routes require `Authorization: Bearer <token>` after signing in.

| Method | Route | Purpose |
|---|---|---|
| POST | `/auth/register` | Create administrator account |
| POST | `/auth/login` | Login and receive JWT |
| GET | `/api/dashboard` | Dashboard totals and recent items |
| GET/POST | `/api/students` | List/search and create students |
| PUT/DELETE | `/api/students/{id}` | Update/delete a student |
| GET/POST | `/api/attendance` | List and mark attendance (one record per student/date) |
| GET/POST | `/api/marks` | List and add marks |
| DELETE | `/api/marks/{id}` | Delete a mark record |
| GET/POST | `/api/fees` | List and add fee records |
| DELETE | `/api/fees/{id}` | Delete a fee record |
| GET/POST | `/api/notifications` | List and publish announcements |
| DELETE | `/api/notifications/{id}` | Delete an announcement |
| GET | `/api/reports/students.csv` | Download student CSV |
| GET | `/api/reports/fees.csv` | Download fee CSV |

## Docker run
Build the backend jar first, then start the stack:

```bash
./mvnw clean package -DskipTests
docker compose up --build
```

Backend is exposed on port 8081. Run the React frontend separately using the steps above, or deploy the frontend build to a static web server.

## Important production notes
- Change the database password and `JWT_SECRET` before deployment. Keep secrets out of source control.
- Registration is intentionally open for the first local administrator setup. For a public deployment, disable public registration after creating the admin and add role/ownership checks.
- This is a practical starter application, not a production-hardened college ERP. It currently has one administrator role; student self-service login, password reset, payment gateway integration, email/SMS delivery and PDF generation are not implemented. Reports currently export CSV, which opens in Excel.
- Student deletion currently deletes the student row only. Review related attendance, marks and fee rows before deleting real records.
