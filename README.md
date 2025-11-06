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

- [VULNERABILITIES.md](VULNERABILITIES.md) - Detailed documentation of all vulnerabilities and testing instructions
- [CODEQL_RESULTS.md](CODEQL_RESULTS.md) - CodeQL test results showing all detected vulnerabilities

## CodeQL Test Results

✅ CodeQL successfully detected **6 out of 6** intentional vulnerabilities:
- 5 Java vulnerabilities (SQL Injection, Command Injection, Path Traversal, Unsafe Deserialization, XSS)
- 1 JavaScript vulnerability (DOM-based XSS)
