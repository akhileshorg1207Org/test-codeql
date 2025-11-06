# CodeQL Test Results

## Summary

CodeQL successfully detected **6 out of 6** intentional vulnerabilities in the TODO application.

## Detected Vulnerabilities

### Java Backend (5 vulnerabilities)

1. **SQL Injection** - `java/sql-injection`
   - File: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
   - Line: 41
   - Method: `searchTodos()`
   - Description: Query depends on user-provided value

2. **Command Injection** - `java/command-line-injection`
   - File: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
   - Line: 52
   - Method: `exportTodos()`
   - Description: Command line depends on user-provided value

3. **Path Traversal** - `java/path-injection`
   - File: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
   - Line: 69
   - Method: `readFile()`
   - Description: Path depends on user-provided value

4. **Unsafe Deserialization** - `java/unsafe-deserialization`
   - File: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
   - Line: 90
   - Method: `importTodo()`
   - Description: Unsafe deserialization depends on user-provided value

5. **Cross-Site Scripting (XSS)** - `java/xss`
   - File: `backend/src/main/java/com/example/todoapp/controller/TodoController.java`
   - Line: 104
   - Method: `getMessage()`
   - Description: Cross-site scripting vulnerability due to user-provided value

### JavaScript Frontend (1 vulnerability)

1. **DOM-based XSS** - `js/xss-through-dom`
   - File: `frontend/src/App.js`
   - Line: 122
   - Description: DOM text is reinterpreted as HTML without escaping meta-characters (dangerouslySetInnerHTML)

## Test Status

✅ **All intentional vulnerabilities were successfully detected by CodeQL**

## Conclusion

The TODO application successfully demonstrates CodeQL's capability to detect common security vulnerabilities in both Java (backend) and JavaScript (frontend) code. This test repository can be used to:

1. Verify CodeQL is properly configured in your GitHub repository
2. Understand how CodeQL reports different types of vulnerabilities
3. Learn about common security issues and how to identify them
4. Test CodeQL query customizations

For detailed information about each vulnerability and how to fix them, see [VULNERABILITIES.md](VULNERABILITIES.md).
