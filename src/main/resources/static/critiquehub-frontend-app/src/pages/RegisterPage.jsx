import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Auth.css"; // Используем те же стили, что и для логина

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
const apiClient = axios.create({ baseURL: API_BASE_URL });

export default function RegisterPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    // 1. Валидация Username (от 3 до 50 символов)
    if (username.length < 3 || username.length > 50) {
      setError("Имя пользователя должно быть от 3 до 50 символов");
      return;
    }

    // 2. Валидация Пароля под требования бэкенда (минимум 8 символов)
    if (password.length < 8) {
      setError("Пароль не должен быть меньше 8 символов");
      return;
    }

    setLoading(true);

    try {
      const response = await apiClient.post("/users", {
        username,
        email,
        password,
      });

      const userData = response.data;
      login(userData);
      navigate("/");
    } catch (err) {
      console.error("Ошибка при регистрации:", err);
      setError(
        err.response?.data?.message || "Не удалось зарегистрироваться. Возможно, email или имя уже заняты."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleRegister}>
        <h2 className="auth-title">Регистрация в CritiqueHub</h2>

        <div className="form-group">
          <label>Имя пользователя</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="john_doe"
            required
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="john@example.com"
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
          {loading ? "Создание аккаунта..." : "Зарегистрироваться"}
        </button>

        <p style={{ marginTop: "15px", textAlign: "center", color: "#aaa", fontSize: "14px" }}>
          Уже есть аккаунт? <Link to="/login" style={{ color: "#007bff", textDecoration: "none" }}>Войти</Link>
        </p>
      </form>
    </div>
  );
}
