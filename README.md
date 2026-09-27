# QHXPro Admin Portal

Java 21 Spring Boot API with a React dashboard for a superadmin login flow.

## Database

This project uses PostgreSQL.

No Flyway is used. Spring Boot connects to PostgreSQL and Hibernate syncs the tables automatically with:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## DigitalOcean Droplet Deploy

Use the included `docker-compose.yml` to run PostgreSQL on the same droplet. This does not use DigitalOcean Managed Databases.

```powershell
copy .env.example .env
```

Edit `.env` and set strong values for `POSTGRES_PASSWORD` and `SUPERADMIN_PASSWORD`, then run:

```powershell
docker compose up -d --build
```

PostgreSQL data is stored in the Docker volume `postgres_data`, so it remains after container restarts.

## Backend

```powershell
cd backend
$env:DATABASE_URL='jdbc:postgresql://localhost:5432/qhxpro'
$env:DATABASE_USERNAME='qhxpro'
$env:DATABASE_PASSWORD='qhxpro'
$env:SUPERADMIN_PASSWORD='change-this-password'
mvn spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Frontend

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

The frontend runs on `http://localhost:5173`.

Default username:

```text
superadmin
```

Set the password with `SUPERADMIN_PASSWORD`. The app hashes it before saving or updating the superadmin account in PostgreSQL.
