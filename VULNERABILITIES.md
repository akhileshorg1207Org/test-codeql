# TODO Application with Intentional Vulnerabilities for CodeQL Testing

⚠️ **WARNING**: This application contains intentional security vulnerabilities for testing purposes. **DO NOT USE IN PRODUCTION!**

## Overview

This is a simple TODO application built with:
- **Backend**: Java Spring Boot
- **Frontend**: React
- **Database**: H2 (in-memory)

The application contains multiple security vulnerabilities designed to test CodeQL's ability to detect common security issues.

## Architecture

```
test-codeql/
├── backend/               # Java Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/todoapp/
│   │       │   ├── TodoApplication.java       # Main application
│   │       │   ├── controller/
│   │       │   │   └── TodoController.java    # REST API with vulnerabilities
│   │       │   ├── model/
│   │       │   │   └── Todo.java              # Entity model
│   │       │   └── repository/
│   │       │       └── TodoRepository.java    # JPA repository
│   │       └── resources/
│   │           └── application.properties     # Configuration
│   └── pom.xml                                # Maven dependencies
└── frontend/              # React frontend
    ├── src/
    │   ├── App.js                             # Main React component
    │   ├── App.css                            # Styles
    │   ├── index.js                           # Entry point
    │   └── index.css                          # Global styles
    ├── public/
    │   └── index.html                         # HTML template
    └── package.json                           # NPM dependencies
```

## Intentional Vulnerabilities

### Backend (Java) Vulnerabilities

#### 1. SQL Injection (CWE-89)
**Location**: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
- **Method**: `searchTodos()`
- **Line**: Approximately line 38
- **Description**: User input is directly concatenated into a SQL query without parameterization
- **Vulnerable Code**:
  ```java
  String sqlQuery = "SELECT * FROM todos WHERE title LIKE '%" + keyword + "%'";
  ```
- **Attack Example**: `keyword='; DROP TABLE todos; --`
- **CodeQL Query**: Should detect with `java/sql-injection`

#### 2. Command Injection (CWE-78)
**Location**: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
- **Method**: `exportTodos()`
- **Line**: Approximately line 48
- **Description**: User input is used directly in Runtime.exec() command
- **Vulnerable Code**:
  ```java
  String command = "echo 'Exporting todos to " + filename + "'";
  Process process = Runtime.getRuntime().exec(command);
  ```
- **Attack Example**: `filename=test.txt'; rm -rf / #`
- **CodeQL Query**: Should detect with `java/command-injection`

#### 3. Path Traversal (CWE-22)
**Location**: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
- **Method**: `readFile()`
- **Line**: Approximately line 63
- **Description**: User can specify arbitrary file paths without validation
- **Vulnerable Code**:
  ```java
  File file = new File("/tmp/" + path);
  BufferedReader reader = new BufferedReader(new FileReader(file));
  ```
- **Attack Example**: `path=../../etc/passwd`
- **CodeQL Query**: Should detect with `java/path-injection`

#### 4. Insecure Deserialization (CWE-502)
**Location**: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
- **Method**: `importTodo()`
- **Line**: Approximately line 79
- **Description**: Deserializing untrusted data without validation
- **Vulnerable Code**:
  ```java
  ObjectInputStream ois = new ObjectInputStream(bis);
  Todo todo = (Todo) ois.readObject();
  ```
- **Attack Example**: Malicious serialized object
- **CodeQL Query**: Should detect with `java/unsafe-deserialization`

#### 5. Cross-Site Scripting (XSS) (CWE-79)
**Location**: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
- **Method**: `getMessage()`
- **Line**: Approximately line 92
- **Description**: User input directly reflected in HTML response
- **Vulnerable Code**:
  ```java
  String message = "<html><body><h1>Hello " + name + "!</h1></body></html>";
  ```
- **Attack Example**: `name=<script>alert('XSS')</script>`
- **CodeQL Query**: Should detect with `java/xss`

### Frontend (JavaScript/React) Vulnerabilities

#### 6. DOM-based XSS (CWE-79)
**Location**: `frontend/src/App.js`
- **Component**: `App`
- **Line**: Approximately line 121
- **Description**: Using `dangerouslySetInnerHTML` with unsanitized user input
- **Vulnerable Code**:
  ```javascript
  <div 
    className="message"
    dangerouslySetInnerHTML={{ __html: message }}
  />
  ```
- **Attack Example**: Search keyword containing `<script>alert('XSS')</script>`
- **CodeQL Query**: Should detect with `js/xss-through-dom`

#### 7. Client-Side Security Issue
**Location**: `frontend/src/App.js`
- **Function**: `exportTodos()`
- **Line**: Approximately line 100
- **Description**: Prompting user for sensitive input without validation
- **Vulnerable Code**:
  ```javascript
  const filename = prompt('Enter filename:');
  const response = await axios.get(`${API_BASE_URL}/export`, {
    params: { filename: filename }
  });
  ```
- **Issue**: No client-side validation before sending to vulnerable backend endpoint

## Running the Application

### Prerequisites
- Java 11 or higher
- Node.js 14 or higher
- Maven 3.6 or higher

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend will start on `http://localhost:3000`

## API Endpoints

### Safe Endpoints
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get todo by ID
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo

### Vulnerable Endpoints (For Testing)
- `GET /api/todos/search?keyword={keyword}` - SQL Injection
- `GET /api/todos/export?filename={filename}` - Command Injection
- `GET /api/todos/file?path={path}` - Path Traversal
- `POST /api/todos/import` - Insecure Deserialization
- `GET /api/todos/message?name={name}` - XSS

## Testing CodeQL

### Setup CodeQL Analysis

1. Enable CodeQL in your GitHub repository:
   - Go to Repository Settings > Security > Code security and analysis
   - Enable "CodeQL analysis"

2. Configure CodeQL workflow:
   - CodeQL should automatically detect Java and JavaScript code
   - It will analyze the code on push and pull requests

### Expected CodeQL Alerts

CodeQL should detect and report the following vulnerabilities:

**Java Alerts:**
- SQL Injection (High severity)
- Command Injection (High severity)
- Path Traversal (High severity)
- Unsafe Deserialization (High severity)
- Cross-Site Scripting (Medium severity)

**JavaScript Alerts:**
- DOM-based XSS (Medium severity)
- Unsafe use of dangerouslySetInnerHTML

### Manual Testing

You can manually test the vulnerabilities:

1. **SQL Injection**:
   ```bash
   curl "http://localhost:8080/api/todos/search?keyword=test%27%20OR%20%271%27=%271"
   ```

2. **Command Injection**:
   ```bash
   curl "http://localhost:8080/api/todos/export?filename=test.txt;ls"
   ```

3. **Path Traversal**:
   ```bash
   curl "http://localhost:8080/api/todos/file?path=../../../etc/hosts"
   ```

4. **XSS**:
   ```bash
   curl "http://localhost:8080/api/todos/message?name=<script>alert('XSS')</script>"
   ```

## Remediation Examples

### How to Fix SQL Injection
```java
// VULNERABLE
String sqlQuery = "SELECT * FROM todos WHERE title LIKE '%" + keyword + "%'";

// SECURE - Use parameterized query
String sqlQuery = "SELECT t FROM Todo t WHERE t.title LIKE :keyword";
Query query = entityManager.createQuery(sqlQuery, Todo.class);
query.setParameter("keyword", "%" + keyword + "%");
```

### How to Fix Command Injection
```java
// VULNERABLE
Process process = Runtime.getRuntime().exec(command);

// SECURE - Avoid system commands or use ProcessBuilder with validation
// Better: Use Java libraries instead of system commands
```

### How to Fix Path Traversal
```java
// VULNERABLE
File file = new File("/tmp/" + path);

// SECURE - Validate and sanitize paths
Path basePath = Paths.get("/tmp").toRealPath();
Path requestedPath = basePath.resolve(path).normalize();
if (!requestedPath.startsWith(basePath)) {
    throw new SecurityException("Invalid path");
}
```

### How to Fix XSS
```java
// VULNERABLE
String message = "<html><body><h1>Hello " + name + "!</h1></body></html>";

// SECURE - Use proper encoding/escaping
import org.springframework.web.util.HtmlUtils;
String message = "<html><body><h1>Hello " + HtmlUtils.htmlEscape(name) + "!</h1></body></html>";
```

### How to Fix DOM-based XSS (React)
```javascript
// VULNERABLE
<div dangerouslySetInnerHTML={{ __html: message }} />

// SECURE - Use text content instead
<div>{message}</div>

// OR use a sanitization library like DOMPurify
import DOMPurify from 'dompurify';
<div dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(message) }} />
```

## License

This is a demonstration project for security testing purposes only.

## Disclaimer

This application is designed for educational and testing purposes only. It contains intentional security vulnerabilities and should NEVER be deployed to production or exposed to the internet. Use in controlled environments only.
