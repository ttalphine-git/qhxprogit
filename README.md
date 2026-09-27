# QHXPro Admin Portal

Java 21 Spring Boot API with a React dashboard for a superadmin login flow.

## Backend

```powershell
cd backend
$env:SUPERADMIN_PASSWORD='{noop}change-this-password'
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

Set the password with `SUPERADMIN_PASSWORD`. Spring Security's delegating password format is supported, for example `{bcrypt}...` or `{noop}...` for local development.
