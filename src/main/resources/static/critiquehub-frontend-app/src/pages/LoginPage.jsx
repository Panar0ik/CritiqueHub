import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Auth.css"; // Твои стили для авторизации

// Настройка клиента axios (убедись, что адрес бэкенда совпадает)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
const apiClient = axios.create({ baseURL: API_BASE_URL });

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth(); // Достаем метод login из контекста
  const navigate = useNavigate(); // Хук для программного перенаправления

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // Отправляем POST-запрос на бэкенд с телом { email, password }
      const response = await apiClient.post("/users/login", {
        email,
        password,
      });

      // Предполагаем, что бэкенд возвращает объект пользователя, например: { id, email, name, token }
      const userData = response.data;

      // Сохраняем пользователя в контекст (после этого Header сразу обновится)
      login(userData);

      // Уводим пользователя на главную страницу
      navigate("/");
    } catch (err) {
      console.error("Ошибка при входе:", err);
      // Выводим ошибку от бэкенда или дефолтное сообщение
      setError(
        err.response?.data?.message || "Неверный email или пароль. Попробуйте снова."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <form className="auth-form" onSubmit={handleLogin}>
        <h2>Войти в CritiqueHub</h2>

        {error && <div className="auth-error-message">{error}</div>}

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

        <button type="submit" className="auth-submit-btn" disabled={loading}>
          {loading ? "Вход..." : "Войти"}
        </button>
      </form>
    </div>
  );
}
