import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

/**
 * INTENTIONAL VULNERABILITIES FOR CODEQL TESTING
 * This React app contains security vulnerabilities:
 * 1. DOM-based XSS via dangerouslySetInnerHTML
 * 2. Insecure direct object reference
 * 3. Client-side validation only
 * DO NOT USE THIS CODE IN PRODUCTION!
 */

const API_BASE_URL = 'http://localhost:8080/api/todos';

function App() {
  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchTodos();
  }, []);

  const fetchTodos = async () => {
    try {
      const response = await axios.get(API_BASE_URL);
      setTodos(response.data);
    } catch (error) {
      console.error('Error fetching todos:', error);
    }
  };

  const createTodo = async (e) => {
    e.preventDefault();
    try {
      await axios.post(API_BASE_URL, {
        title: title,
        description: description,
        completed: false
      });
      setTitle('');
      setDescription('');
      fetchTodos();
      setMessage('TODO created successfully!');
    } catch (error) {
      console.error('Error creating todo:', error);
      setMessage('Error creating TODO');
    }
  };

  const toggleTodo = async (id, completed) => {
    try {
      const todo = todos.find(t => t.id === id);
      await axios.put(`${API_BASE_URL}/${id}`, {
        ...todo,
        completed: !completed
      });
      fetchTodos();
    } catch (error) {
      console.error('Error updating todo:', error);
    }
  };

  const deleteTodo = async (id) => {
    try {
      await axios.delete(`${API_BASE_URL}/${id}`);
      fetchTodos();
      setMessage('TODO deleted successfully!');
    } catch (error) {
      console.error('Error deleting todo:', error);
    }
  };

  // VULNERABILITY 1: DOM-based XSS
  // Using dangerouslySetInnerHTML with user input without sanitization
  const searchTodos = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/search`, {
        params: { keyword: searchKeyword }
      });
      setTodos(response.data);
      // VULNERABLE: Directly inserting user input into HTML
      setMessage(`<div>Search results for: <strong>${searchKeyword}</strong></div>`);
    } catch (error) {
      console.error('Error searching todos:', error);
    }
  };

  // VULNERABILITY 2: Exposing sensitive operations without proper validation
  const exportTodos = async () => {
    try {
      // VULNERABLE: User can control the filename parameter
      const filename = prompt('Enter filename:');
      if (filename) {
        const response = await axios.get(`${API_BASE_URL}/export`, {
          params: { filename: filename }
        });
        alert(response.data);
      }
    } catch (error) {
      console.error('Error exporting todos:', error);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>TODO Application</h1>
        <p style={{color: 'red', fontSize: '12px'}}>
          ⚠️ This app contains intentional security vulnerabilities for CodeQL testing
        </p>
      </header>

      <div className="container">
        {/* Message Display - VULNERABLE: dangerouslySetInnerHTML */}
        {message && (
          <div 
            className="message"
            dangerouslySetInnerHTML={{ __html: message }}
          />
        )}

        {/* Create TODO Form */}
        <div className="create-form">
          <h2>Create New TODO</h2>
          <form onSubmit={createTodo}>
            <input
              type="text"
              placeholder="Title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />
            <textarea
              placeholder="Description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows="3"
            />
            <button type="submit">Add TODO</button>
          </form>
        </div>

        {/* Search Functionality */}
        <div className="search-section">
          <h2>Search TODOs</h2>
          <input
            type="text"
            placeholder="Search keyword"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
          />
          <button onClick={searchTodos}>Search</button>
          <button onClick={fetchTodos}>Show All</button>
          <button onClick={exportTodos}>Export</button>
        </div>

        {/* TODO List */}
        <div className="todo-list">
          <h2>TODO List</h2>
          {todos.length === 0 ? (
            <p>No todos found. Create one!</p>
          ) : (
            <ul>
              {todos.map((todo) => (
                <li key={todo.id} className={todo.completed ? 'completed' : ''}>
                  <div className="todo-content">
                    <h3>{todo.title}</h3>
                    <p>{todo.description}</p>
                  </div>
                  <div className="todo-actions">
                    <button 
                      onClick={() => toggleTodo(todo.id, todo.completed)}
                      className={todo.completed ? 'incomplete-btn' : 'complete-btn'}
                    >
                      {todo.completed ? 'Mark Incomplete' : 'Mark Complete'}
                    </button>
                    <button 
                      onClick={() => deleteTodo(todo.id)}
                      className="delete-btn"
                    >
                      Delete
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}

export default App;
