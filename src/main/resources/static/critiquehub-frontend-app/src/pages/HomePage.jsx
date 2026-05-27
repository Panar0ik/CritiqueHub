import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function HomePage() {
  const [tags, setTags] = useState([]);
  const [spaces, setSpaces] = useState([]);
  const [favorites, setFavorites] = useState([]);

  // Стейт для хранения выбранного тега
  const [selectedTag, setSelectedTag] = useState(null);

  const { user } = useAuth();
  const userId = user?.id || user?.userId;

  // 1. Загрузка боковых панелей (Теги и Избранное)
  useEffect(() => {
    apiClient.get("/tags").then(res => setTags(res.data.slice(0, 10)));

    if (userId) {
      apiClient.get(`/users/${userId}/favorites`)
        .then(res => setFavorites(res.data))
        .catch(err => console.error("Не удалось загрузить избранное:", err));
    } else {
      setFavorites([]);
    }
  }, [userId]);

    useEffect(() => {
      if (selectedTag) {
        apiClient.get(`/spaces/search?tag=${encodeURIComponent(selectedTag)}`)
          .then(res => {
            const results = res.data.content || res.data || [];
            setSpaces(results);
          })
          .catch(err => {
            console.error("Ошибка при поиске по тегу:", err);
            setSpaces([]);
          });
      } else {
        apiClient.get("/spaces")
          .then(res => {
            const results = res.data.content || res.data || [];
            setSpaces(results);
          })
          .catch(err => {
            console.error("Ошибка при загрузке пространств:", err);
            setSpaces([]);
          });
      }
    }, [selectedTag]);

  return (
    <div className="body-container">

      {/* ЛЕВАЯ КОЛОНКА */}
      <aside className="sidebar">
        <h3 className="sidebar-title">ИЗБРАННОЕ</h3>
        <div className="favorites-content">
           {!user ? (
              <p className="favorites-empty-text">
                Чтобы увидеть избранное, <Link to="/login" className="login-link">войдите</Link>.
              </p>
           ) : favorites.length > 0 ? (
              <ul className="tags-list">
                {favorites.map(fav => (
                  <li key={fav.id} className="fav-item-sidebar">
                    {/* ИСПРАВЛЕНО: Ссылка ведет на страницу пространства во фронтенде */}
                    <Link to={`/spaces/${fav.id}`}>★ {fav.name}</Link>
                  </li>
                ))}
              </ul>
           ) : (
              <p className="favorites-empty-text">Пока избранных нет.</p>
           )}
        </div>
      </aside>
      <div className="vertical-line" />

      {/* ЦЕНТРАЛЬНАЯ КОЛОНКА */}
      <main className="content">
        <div className="main-content-wrapper">
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "15px" }}>
            <h2 className="main-title" style={{ margin: 0 }}>
              {selectedTag ? `Пространства с тегом #${selectedTag}` : "Пространства"}
            </h2>

            {/* Кнопка сброса фильтра */}
            {selectedTag && (
              <button
                onClick={() => setSelectedTag(null)}
                style={{
                  background: "#222",
                  color: "#007bff",
                  border: "1px solid #333",
                  padding: "6px 12px",
                  borderRadius: "6px",
                  cursor: "pointer",
                  fontWeight: "600",
                  fontSize: "13px"
                }}
              >
                Показать все
              </button>
            )}
          </div>

          <ul className="spaces-list-main">
            {spaces.map(space => (
              <Link to={`/spaces/${space.id}`} key={space.id} className="space-card-link">
                 <div className="space-content">
                   <h3 className="space-name">{space.name}</h3>
                   <p className="space-description">{space.description || "Описание отсутствует"}</p>
                   <div className="space-tags">
                     {space.tagNames && space.tagNames.map((tag, i) => (
                       <span key={i} className="space-tag-item">#{tag}</span>
                     ))}
                   </div>
                 </div>
              </Link>
            ))}
            {spaces.length === 0 && (
              <p style={{ color: "#777", marginTop: "20px" }}>Пространств с таким тегом пока не создано.</p>
            )}
          </ul>
        </div>
      </main>

      <div className="vertical-line" />

      {/* ПРАВАЯ КОЛОНКА (Интерактивные теги) */}
      <aside className="sidebar-right">
        <h3 className="sidebar-title">ТЕГИ:</h3>
        <ul className="tags-list">
          {tags.map(tag => {
            const isSelected = selectedTag === tag.name;
            return (
              <li
                key={tag.id}
                onClick={() => setSelectedTag(tag.name)}
                style={{
                  cursor: "pointer",
                  padding: "6px 0",
                  color: isSelected ? "#007bff" : "#aaa",
                  fontWeight: isSelected ? "bold" : "normal",
                  transition: "color 0.2s ease"
                }}
                onMouseEnter={(e) => !isSelected && (e.target.style.color = "#fff")}
                onMouseLeave={(e) => !isSelected && (e.target.style.color = "#aaa")}
              >
                #{tag.name}
              </li>
            );
          })}
        </ul>
      </aside>
    </div>
  );
}
