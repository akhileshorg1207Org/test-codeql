# test-codeql
Java Backend + ReactJS Frontend

## ⚠️ WARNING: Intentional Vulnerabilities

This repository contains a TODO application with **intentional security vulnerabilities** for testing CodeQL's detection capabilities. **DO NOT USE IN PRODUCTION!**

## Quick Start

### Backend (Java Spring Boot)
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend (React)
```bash
cd frontend
npm install
npm start
```

## Documentation

See [VULNERABILITIES.md](VULNERABILITIES.md) for detailed documentation of all vulnerabilities and testing instructions.
