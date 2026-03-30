# Audit Backend Project

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. Install PostgreSQL and create a database named `audit_db`
2. Update database credentials in `src/main/resources/application.properties` if needed:
   ```
   spring.datasource.username=your_postgres_username
   spring.datasource.password=your_postgres_password
   ```

### Running the Application
1. Clone the repository
2. Run `mvn clean install`
3. Start the application: `mvn spring-boot:run`
4. The application will automatically create database tables

### Creating Test Users
After the application starts and creates tables, run the SQL script `setup.sql` to insert test users:

**Test accounts:**
- Admin: `admin@example.com` / `password`
- Auditor: `auditeur@example.com` / `password`
- Client: `client@example.com` / `password`

### API Endpoints
- Login: `POST /api/auth/login`
- Base URL: `http://localhost:8080`

### Frontend Integration
The backend is configured with CORS to allow all origins (`*`). Ensure your frontend sends requests to the correct endpoints.