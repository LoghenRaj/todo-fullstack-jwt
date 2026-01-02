import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Todos from "./pages/Todos.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import { isLoggedIn } from "./auth.js";

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={<Navigate to={isLoggedIn() ? "/todos" : "/login"} replace />}
      />

      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route
        path="/todos"
        element={
          <ProtectedRoute>
            <Todos />
          </ProtectedRoute>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
