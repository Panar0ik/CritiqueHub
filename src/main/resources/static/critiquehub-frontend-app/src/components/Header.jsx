import React from "react";
import { Link } from "react-router-dom";
// 1. Обязательно импортируем хук авторизации
import { useAuth } from "../context/AuthContext";

export default function Header() {
  // 2. Достаем user и logout из контекста.
  // Без этой строчки React не знает, что такое user и logout!
  const { user, logout } = useAuth();

  return (
    <header className="header">
      <Link to="/" style={{ textDecoration: 'none' }}>
        <h1 className="logo">CritiqueHub</h1>
      </Link>

      <div className="search-wrapper">
        <div className="search-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </div>
        <input type="text" className="search-input" placeholder="Поиск пространств..." />
      </div>

      {/* 3. Теперь этот блок отработает идеально, так как переменные объявлены выше */}
      <div className="auth-buttons">
        {user ? (
          <>
            <Link to="/profile" className="login-btn">Личный кабинет</Link>
            <button onClick={logout} className="register-btn">Выйти</button>
          </>
        ) : (
          <>
            <Link to="/login" className="login-btn">Войти</Link>
            <Link to="/register" className="register-btn">Зарегистрироваться</Link>
          </>
        )}
      </div>
    </header>
  );
}
