import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Auth.css";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
const apiClient = axios.create({ baseURL: API_BASE_URL });

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await apiClient.post("/users/login", {
        email,
        password,
      });

      const userData = response.data;
      login(userData);
      navigate("/");
    } catch (err) {
      console.error("Ошибка при входе:", err);

      const serverMessage = err.response?.data?.message || "";

      if (serverMessage.includes("Email is required")) {
        setError("Пожалуйста, введите Email.");
      } else if (serverMessage.includes("Email should be valid")) {
        setError("Некорректный формат Email (например: user@example.com).");
      } else if (serverMessage.includes("Password is required")) {
        setError("Пожалуйста, введите пароль.");
      } else if (serverMessage.includes("User not found") || err.response?.status === 404) {
        setError("Пользователь с таким Email не найден.");
      } else if (err.response?.status === 401 || err.response?.status === 400) {
        setError("Неверный email или пароль. Попробуйте снова.");
      } else {
        setError("Произошла ошибка при входе. Попробуйте позже.");
      }

    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleLogin}>
        <h2 className="auth-title">Войти в CritiqueHub</h2>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="example@mail.com"
            required
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label>Пароль</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
            required
            disabled={loading}
          />
        </div>

        {error && <div className="auth-error-message">{error}</div>}

        <button type="submit" className="auth-submit-btn" disabled={loading}>
          {loading ? "Вход..." : "Войти"}
        </button>
      </form>
    </div>
  );
}
