import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api.js";
import { setToken } from "../auth.js";

export default function Register() {
  const nav = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function onSubmit(e) {
    e.preventDefault();
    setError("");

    try {
      const res = await api.post("/api/auth/register", { username, password });
      setToken(res.data.token);
      nav("/todos");
    } catch (err) {
      const msg =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        "Register failed";
      setError(msg);
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Register</h1>

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
              autoComplete="new-password"
              required
              minLength={6}
            />
          </label>

          {error && <div className="error">{error}</div>}

          <button type="submit">Create account</button>
        </form>

        <p className="muted">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}
