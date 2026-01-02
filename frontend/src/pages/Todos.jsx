import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api.js";
import { clearToken } from "../auth.js";

export default function Todos() {
  const nav = useNavigate();

  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  async function loadTodos() {
    setError("");
    setLoading(true);
    try {
      const res = await api.get("/api/todos");
      setTodos(res.data);
    } catch (e) {
      console.error(e);
      setError("Failed to load todos");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadTodos();
  }, []);

  async function createTodo(e) {
    e.preventDefault();
    const t = title.trim();
    if (!t) return;

    setError("");
    try {
      const res = await api.post("/api/todos", { title: t });
      setTodos((prev) => [res.data, ...prev]);
      setTitle("");
    } catch (e) {
      console.error(e);
      setError("Failed to create todo");
    }
  }

  async function toggleTodo(todo) {
    setError("");
    try {
      const res = await api.put(`/api/todos/${todo.id}`, {
        completed: !todo.completed,
      });
      setTodos((prev) => prev.map((x) => (x.id === todo.id ? res.data : x)));
    } catch (e) {
      console.error(e);
      setError("Failed to update todo");
    }
  }

  async function deleteTodo(id) {
    setError("");
    try {
      await api.delete(`/api/todos/${id}`);
      setTodos((prev) => prev.filter((x) => x.id !== id));
    } catch (e) {
      console.error(e);
      setError("Failed to delete todo");
    }
  }

  function logout() {
    clearToken();
    nav("/login");
  }

  return (
    <div style={{ maxWidth: 720, margin: "40px auto", padding: 16 }}>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <h2>Todos</h2>
        <button onClick={logout}>Logout</button>
      </div>

      {error ? (
        <div style={{ margin: "12px 0", color: "crimson" }}>{error}</div>
      ) : null}

      <form onSubmit={createTodo} style={{ display: "flex", gap: 8 }}>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="New todo title..."
          style={{ flex: 1, padding: 10 }}
        />
        <button type="submit">Add</button>
      </form>

      <div style={{ marginTop: 16 }}>
        {loading ? (
          <div>Loading...</div>
        ) : todos.length === 0 ? (
          <div>No todos yet.</div>
        ) : (
          <ul style={{ listStyle: "none", padding: 0 }}>
            {todos.map((t) => (
              <li
                key={t.id}
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  padding: "10px 0",
                  borderBottom: "1px solid #ddd",
                }}
              >
                <label style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  <input
                    type="checkbox"
                    checked={t.completed}
                    onChange={() => toggleTodo(t)}
                  />
                  <span style={{ textDecoration: t.completed ? "line-through" : "none" }}>
                    {t.title}
                  </span>
                </label>

                <button onClick={() => deleteTodo(t.id)}>Delete</button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}
