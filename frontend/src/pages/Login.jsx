import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api.js";
import { setToken } from "../auth.js";

export default function Login() {
  const nav = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    try {
      const res = await api.post("/api/auth/login", { username, password });
      setToken(res.data.token);
      nav("/todos");
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Login failed";
      setError(msg);
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Login</h1>

        <form onSubmit={onSubmit} className="form">
          <label>
            Username
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </label>

          <label>
            Password
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              autoComplete="current-password"
              required
            />
          </label>

          {error && <div className="error">{error}</div>}

          <button type="submit">Login</button>
        </form>

        <p className="muted">
          No account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
}
