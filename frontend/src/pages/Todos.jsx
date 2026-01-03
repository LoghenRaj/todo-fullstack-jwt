import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api.js";
import { clearToken } from "../auth.js";

export default function Todos() {
  const nav = useNavigate();

  const [todos, setTodos] = useState([]);
  const [title, setTitle] = useState("");

  const [q, setQ] = useState("");
  const [filter, setFilter] = useState("all"); // all | open | done
  const [sortBy, setSortBy] = useState("id"); // id | title | completed
  const [dir, setDir] = useState("desc"); // asc | desc

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const completedParam = useMemo(() => {
    if (filter === "open") return false;
    if (filter === "done") return true;
    return undefined; // omit param for "all"
  }, [filter]);

  async function loadTodos(signal) {
    setError("");
    setLoading(true);

    try {
      const params = {
        q: q.trim() ? q.trim() : undefined,
        completed: completedParam,
        sort: sortBy,
        dir,
      };

      const res = await api.get("/api/todos", { params, signal });
      setTodos(res.data);
    } catch (e) {
      // Axios abort/cancel can throw; ignore those
      if (e?.name === "CanceledError" || e?.code === "ERR_CANCELED") return;

      console.error(e);
      setError("Failed to load todos");
    } finally {
      setLoading(false);
    }
  }

  // Small debounce so typing in search doesn’t spam the backend
  useEffect(() => {
    const controller = new AbortController();
    const t = setTimeout(() => loadTodos(controller.signal), 250);
    return () => {
      clearTimeout(t);
      controller.abort();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [q, completedParam, sortBy, dir]);

  async function createTodo(e) {
    e.preventDefault();
    const t = title.trim();
    if (!t) return;

    setError("");
    try {
      await api.post("/api/todos", { title: t });
      setTitle("");
      await loadTodos();
    } catch (e2) {
      console.error(e2);
      setError("Failed to create todo");
    }
  }

  async function toggleTodo(todo) {
    setError("");
    try {
      await api.put(`/api/todos/${todo.id}`, { completed: !todo.completed });
      await loadTodos();
    } catch (e) {
      console.error(e);
      setError("Failed to update todo");
    }
  }

  async function deleteTodo(id) {
    setError("");
    try {
      await api.delete(`/api/todos/${id}`);
      await loadTodos();
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
    <div className="page">
      <div className="card card-wide">
        <div className="headerRow">
          <div>
            <h1 style={{ marginBottom: 6 }}>Todos</h1>
            <div className="muted" style={{ marginTop: 0 }}>
              Search, filter, and sort your tasks.
            </div>
          </div>

          <button className="secondary" onClick={logout}>
            Logout
          </button>
        </div>

        {error ? <div className="error">{error}</div> : null}

        <div className="toolbar">
          <div className="toolbarItem">
            <label className="toolbarLabel">Search</label>
            <input
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="Search by title..."
            />
          </div>

          <div className="toolbarItem">
            <label className="toolbarLabel">Filter</label>
            <select value={filter} onChange={(e) => setFilter(e.target.value)}>
              <option value="all">All</option>
              <option value="open">Incomplete</option>
              <option value="done">Completed</option>
            </select>
          </div>

          <div className="toolbarItem">
            <label className="toolbarLabel">Sort</label>
            <select value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
              <option value="id">Created (id)</option>
              <option value="title">Title</option>
              <option value="completed">Completed</option>
            </select>
          </div>

          <div className="toolbarItem">
            <label className="toolbarLabel">Direction</label>
            <select value={dir} onChange={(e) => setDir(e.target.value)}>
              <option value="desc">Desc</option>
              <option value="asc">Asc</option>
            </select>
          </div>
        </div>

        <form onSubmit={createTodo} className="row" style={{ marginTop: 14 }}>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="New todo title..."
          />
          <button type="submit">Add</button>
        </form>

        <div style={{ marginTop: 14 }}>
          {loading ? (
            <div className="muted">Loading...</div>
          ) : todos.length === 0 ? (
            <div className="muted">No todos yet.</div>
          ) : (
            <ul className="list">
              {todos.map((t) => (
                <li key={t.id} className="item">
                  <label className="checkbox">
                    <input
                      type="checkbox"
                      checked={t.completed}
                      onChange={() => toggleTodo(t)}
                    />
                    <span className={t.completed ? "done" : ""}>{t.title}</span>
                  </label>

                  <button className="danger" onClick={() => deleteTodo(t.id)}>
                    Delete
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
