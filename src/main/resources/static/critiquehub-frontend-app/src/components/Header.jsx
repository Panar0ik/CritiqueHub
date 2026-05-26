import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  // 1. Создаем стейт для управления показом модального окна
  const [showConfirm, setShowConfirm] = useState(false);

  // Открывает окно подтверждения
  const handleLogoutClick = () => {
    setShowConfirm(true);
  };

  // Выполняет выход при подтверждении
  const handleConfirmLogout = () => {
    setShowConfirm(false);
    logout();
    navigate("/login");
  };

  // Закрывает окно при отмене
  const handleCancelLogout = () => {
    setShowConfirm(false);
  };

  return (
    <header className="header">
      {/* Твой существующий код логотипа, поиска и т.д. */}
      <Link to="/" className="logo" style={{ textDecoration: 'none' }}>CritiqueHub</Link>

      <div className="search-wrapper">
        <span className="search-icon">🔍</span>
        <input type="text" className="search-input" placeholder="Поиск пространств..." />
      </div>

      {/* 2. Обновленный блок кнопок */}
      <div className="auth-buttons">
        {user ? (
          <>
            <Link to="/profile" className="login-btn" style={{ textDecoration: 'none' }}> Личный кабинет </Link>
            {/* Меняем класс на logout-btn для красного свечения и вешаем handleLogoutClick */}
            <button onClick={handleLogoutClick} className="logout-btn"> Выйти </button>
          </>
        ) : (
          <>
            <Link to="/login" className="login-btn" style={{ textDecoration: 'none' }}>Войти</Link>
            <Link to="/register" className="register-btn" style={{ textDecoration: 'none' }}>Зарегистрироваться</Link>
          </>
        )}
      </div>

      {/* 3. Компонент модального окна (отрендерится только если showConfirm === true) */}
      {showConfirm && (
        <div className="modal-overlay" onClick={handleCancelLogout}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Уверены, что хотите выйти?</h3>
            <p>Вы выйдете из своего аккаунта, но все ваши сообщения и избранные пространства сохранятся.</p>

            <div className="modal-buttons">
              <button className="modal-btn modal-btn-confirm" onClick={handleConfirmLogout}>
                Да, выйти
              </button>
              <button className="modal-btn modal-btn-cancel" onClick={handleCancelLogout}>
                Отмена
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
