package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

/**
 * INTENTIONAL VULNERABILITIES FOR CODEQL TESTING
 * This controller contains multiple security vulnerabilities:
 * 1. SQL Injection
 * 2. Command Injection
 * 3. Path Traversal
 * 4. Insecure Deserialization
 * DO NOT USE THIS CODE IN PRODUCTION!
 */
@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TodoController {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private EntityManager entityManager;

    // VULNERABILITY 1: SQL Injection
    // Using string concatenation to build SQL query with user input
    @GetMapping("/search")
    public ResponseEntity<?> searchTodos(@RequestParam String keyword) {
        // VULNERABLE: Direct string concatenation with user input in SQL query
        String sqlQuery = "SELECT * FROM todos WHERE title LIKE '%" + keyword + "%'";
        Query query = entityManager.createNativeQuery(sqlQuery, Todo.class);
        List<Todo> results = query.getResultList();
        return ResponseEntity.ok(results);
    }

    // VULNERABILITY 2: Command Injection
    // Executing system commands with user-controlled input
    @GetMapping("/export")
    public ResponseEntity<?> exportTodos(@RequestParam String filename) throws IOException {
        // VULNERABLE: User input directly used in system command
        String command = "echo 'Exporting todos to " + filename + "'";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return ResponseEntity.ok(output.toString());
    }

    // VULNERABILITY 3: Path Traversal
    // Reading files based on user input without validation
    @GetMapping("/file")
    public ResponseEntity<?> readFile(@RequestParam String path) {
        try {
            // VULNERABLE: User can specify arbitrary file paths
            File file = new File("/tmp/" + path);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();
            return ResponseEntity.ok(content.toString());
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        }
    }

    // VULNERABILITY 4: Insecure Deserialization
    // Deserializing user-provided data without validation
    @PostMapping("/import")
    public ResponseEntity<?> importTodo(@RequestBody byte[] serializedData) {
        try {
            // VULNERABLE: Deserializing untrusted data
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Todo todo = (Todo) ois.readObject();
            ois.close();
            Todo savedTodo = todoRepository.save(todo);
            return ResponseEntity.ok(savedTodo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing todo: " + e.getMessage());
        }
    }

    // VULNERABILITY 5: XSS - Reflecting user input without sanitization
    @GetMapping("/message")
    public ResponseEntity<String> getMessage(@RequestParam String name) {
        // VULNERABLE: User input directly reflected in response (potential for XSS)
        String message = "<html><body><h1>Hello " + name + "!</h1></body></html>";
        return ResponseEntity.ok().header("Content-Type", "text/html").body(message);
    }

    // Safe CRUD operations for comparison
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        return todoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo savedTodo = todoRepository.save(todo);
        return ResponseEntity.ok(savedTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todoDetails) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(todoDetails.getTitle());
                    todo.setDescription(todoDetails.getDescription());
                    todo.setCompleted(todoDetails.isCompleted());
                    return ResponseEntity.ok(todoRepository.save(todo));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todoRepository.delete(todo);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
