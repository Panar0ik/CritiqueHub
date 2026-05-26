import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { Link } from "react-router-dom";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function HomePage() {
  const [tags, setTags] = useState([]);
  const [spaces, setSpaces] = useState([]);
  const [favorites, setFavorites] = useState([]);

  const { user } = useAuth();
  const userId = user?.id || user?.userId; // Получаем ID авторизованного юзера

  useEffect(() => {
    apiClient.get("/tags").then(res => setTags(res.data.slice(0, 10)));
    apiClient.get("/spaces").then(res => setSpaces(res.data));

    if (userId) {
      apiClient.get(`/users/${userId}/favorites`)
        .then(res => {
          setFavorites(res.data);
        })
        .catch(err => {
          console.error("Не удалось загрузить избранное:", err);
        });
    } else {
      setFavorites([]);
    }
  }, [userId]); // Ловим изменения userId (вход/выход)

  return (
    <div className="body-container">

      {/* ЛЕВАЯ КОЛОНКА (Синхронизирована со стилями SpacePage) */}
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
                  /* Применяем те же классы fav-item-sidebar и оборачиваем в Link */
                  <li key={fav.id} className="fav-item-sidebar">
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
          <h2 className="main-title">Пространства</h2>
          <ul className="spaces-list-main">
            {spaces.map(space => (
              <Link
                to={`/spaces/${space.id}`}
                key={space.id}
                className="space-card-link"
              >
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
          </ul>
        </div>
      </main>

      <div className="vertical-line" />

      {/* ПРАВАЯ КОЛОНКА */}
      <aside className="sidebar-right">
        <h3 className="sidebar-title">ТЕГИ:</h3>
        <ul className="tags-list">
          {tags.map(tag => <li key={tag.id}>#{tag.name}</li>)}
        </ul>
      </aside>
    </div>
  );
}
