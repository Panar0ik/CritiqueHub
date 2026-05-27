import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Стейты для логаута
  const [showConfirm, setShowConfirm] = useState(false);

  // Стейты для динамического поиска
  const queryParams = new URLSearchParams(location.search);
  const [searchQuery, setSearchQuery] = useState(queryParams.get("search") || "");
  const [searchResults, setSearchResults] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);

  const dropdownRef = useRef(null);

  // Синхронизация инпута при изменении URL извне
  useEffect(() => {
    const q = new URLSearchParams(location.search).get("search") || "";
    setSearchQuery(q);
  }, [location.search]);

  // Закрытие дропдауна при клике вне поиска
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // Дебаунс (задержка) для запросов к API
  useEffect(() => {
    const trimmed = searchQuery.trim();

    if (!trimmed) {
      setSearchResults([]);
      setShowDropdown(false);
      return;
    }

    const delayDebounceFn = setTimeout(() => {
      // ИНИЦИАЛИЗАЦИЯ ПАРАМЕТРОВ ЗАПРОСА
      let urlParams = "";

      if (trimmed.startsWith("#")) {
        // Если начинается с #, убираем решетку и ищем по тегу
        const cleanTag = trimmed.slice(1).trim();
        if (!cleanTag) {
          setSearchResults([]);
          setShowDropdown(false);
          return;
        }
        urlParams = `tag=${encodeURIComponent(cleanTag)}`;
      } else {
        // Иначе ищем по названию
        urlParams = `name=${encodeURIComponent(trimmed)}`;
      }

      apiClient
        .get(`/spaces/search?${urlParams}`)
        .then((res) => {
          // Наш бэк возвращает Page, поэтому смотрим в .content
          const results = res.data.content || res.data || [];
          setSearchResults(results);
          setShowDropdown(results.length > 0);
        })
        .catch((err) => {
          console.error("Ошибка API поиска:", err);
          setSearchResults([]);
          setShowDropdown(false);
        });
    }, 300);

    return () => clearTimeout(delayDebounceFn);
  }, [searchQuery]);

  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);
    if (!value.trim()) {
      setShowDropdown(false);
    }
  };

  const handleInputFocus = () => {
    if (searchResults.length > 0) {
      setShowDropdown(true);
    }
  };

  const handleResultClick = (spaceId) => {
    setShowDropdown(false);
    setSearchQuery(""); // Очищаем поиск при переходе
    navigate(`/spaces/${spaceId}`);
  };

  const handleLogoutClick = () => setShowConfirm(true);
  const handleCancelLogout = () => setShowConfirm(false);

  const handleConfirmLogout = () => {
    setShowConfirm(false);
    logout();
    navigate("/login");
  };

  return (
    <header className="header">
      <Link to="/" className="logo" style={{ textDecoration: 'none' }}>CritiqueHub</Link>

      <div className="search-wrapper" ref={dropdownRef}>
        <svg
          className="search-icon-svg"
          width="18"
          height="18"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2.5"
          strokeLinecap="round"
          strokeLinejoin="round"
          style={{ transform: "scaleX(-1)", marginRight: "10px" }}
        >
          <circle cx="11" cy="11" r="8"></circle>
          <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
        </svg>
        <input
          type="text"
          className="search-input"
          placeholder="Поиск по названию или #тегу..."
          value={searchQuery}
          onChange={handleSearchChange}
          onFocus={handleInputFocus}
        />

        {/* ВЫПАДАЮЩЕЕ МЕНЮ С РЕЗУЛЬТАТАМИ */}
        {showDropdown && searchResults.length > 0 && (
          <div className="search-dropdown">
            {searchResults.map((space) => (
              <div
                key={space.id}
                className="search-dropdown-item"
                onClick={() => handleResultClick(space.id)}
              >
                <div className="search-item-header">
                  <span className="search-item-name">{space.name}</span>
                  <div className="search-item-tags">
                    {space.tagNames && space.tagNames.map((tag, i) => (
                      <span key={i} className="search-item-tag">#{tag}</span>
                    ))}
                  </div>
                </div>
                {space.description && (
                  <p className="search-item-description">{space.description}</p>
                )}
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="auth-buttons">
        {user ? (
          <>
            <Link to="/profile" className="login-btn" style={{ textDecoration: 'none' }}> {user.username || "Пользователь"} </Link>
            <button onClick={handleLogoutClick} className="logout-btn"> Выйти </button>
          </>
        ) : (
          <>
            <Link to="/login" className="login-btn" style={{ textDecoration: 'none' }}>Войти</Link>
            <Link to="/register" className="register-btn" style={{ textDecoration: 'none' }}>Зарегистрироваться</Link>
          </>
        )}
      </div>

      {showConfirm && (
        <div className="modal-overlay" onClick={handleCancelLogout}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>Уверены, что хотите выйти?</h3>
            <p>Вы выйдете из своего аккаунта, но все ваши сообщения и избранные пространства сохранятся.</p>
            <div className="modal-buttons">
              <button className="modal-btn modal-btn-confirm" onClick={handleConfirmLogout}>Да, выйти</button>
              <button className="modal-btn modal-btn-cancel" onClick={handleCancelLogout}>Отмена</button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
