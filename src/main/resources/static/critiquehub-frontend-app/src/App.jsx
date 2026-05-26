import React, { useState, useEffect } from "react";
import axios from "axios";
import "./App.css";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";
const apiClient = axios.create({ baseURL: API_BASE_URL });

export default function App() {
  const [tags, setTags] = useState([]);
  const [spaces, setSpaces] = useState([]);
  const [posts, setPosts] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    apiClient.get("/tags").then(res => setTags(res.data.slice(0, 10)));
    apiClient.get("/spaces").then(res => setSpaces(res.data));
    apiClient.get("/posts").then(res => setPosts(res.data));
  }, []);

  return (
    <div className="app">
      <header className="header">
        <h1 className="logo">CritiqueHub</h1>
        <div className="search-wrapper">
          <div className="search-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
          </div>
          <input type="text" className="search-input" placeholder="Поиск пространств..." />
        </div>
        <div className="auth-buttons">
          <button className="login-btn">Войти</button>
          <button className="register-btn">Зарегистрироваться</button>
        </div>
      </header>

      <div className="horizontal-line" />

      <div className="body-container">
        {/* ЛЕВАЯ КОЛОНКА (Теги) */}
        <aside className="sidebar">
          <h3 className="sidebar-title">ТЕГИ:</h3>
          <ul className="tags-list">
            {tags.map(tag => <li key={tag.id}>#{tag.name}</li>)}
          </ul>
        </aside>

        <div className="vertical-line" />

        {/* ЦЕНТРАЛЬНАЯ КОЛОНКА (Контент) */}
        <main className="content">
          <div className="main-content-wrapper">
            <h2 className="main-title">Пространства</h2>
            <ul className="spaces-list-main">
              {spaces.map(space => (
                <li key={space.id} className="space-item-main">
                   <div className="space-content">
                     <h3 className="space-name">{space.name}</h3>
                     <p className="space-description">{space.description || "Описание отсутствует"}</p>
                     <div className="space-tags">
                       {space.tagNames && space.tagNames.map((tag, i) => (
                         <span key={i} className="space-tag-item">#{tag}</span>
                       ))}
                     </div>
                   </div>
                </li>
              ))}
            </ul>
          </div>
        </main>

        <div className="vertical-line" />

        {/* ПРАВАЯ КОЛОНКА (Избранное) */}
        <aside className="sidebar-right">
          <h3 className="sidebar-title" style={{ fontSize: "18px" }}>ИЗБРАННОЕ:</h3>
          <div className="favorites-content" style={{ marginTop: "15px" }}>
             {!isLoggedIn ? (
                <p className="favorites-container">Чтобы увидеть избранное, <button className="login-link">войдите</button>.</p>
             ) : favorites.length > 0 ? (
                <ul className="tags-list">
                  {favorites.map(fav => <li key={fav.id} className="fav-item-sidebar">★ {fav.name}</li>)}
                </ul>
             ) : (
                <p className="favorites-container">Пока избранных нет.</p>
             )}
          </div>
        </aside>
      </div>
      </div>
  );
}
