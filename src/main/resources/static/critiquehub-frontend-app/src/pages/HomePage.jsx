import React, { useState, useEffect } from "react";
import axios from "axios";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function HomePage() {
  const [tags, setTags] = useState([]);
  const [spaces, setSpaces] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    apiClient.get("/tags").then(res => setTags(res.data.slice(0, 10)));
    apiClient.get("/spaces").then(res => setSpaces(res.data));
  }, []);

  return (
    <div className="body-container">
      {/* ЛЕВАЯ КОЛОНКА */}
      <aside className="sidebar">
        <h3 className="sidebar-title">ТЕГИ:</h3>
        <ul className="tags-list">
          {tags.map(tag => <li key={tag.id}>#{tag.name}</li>)}
        </ul>
      </aside>

      <div className="vertical-line" />

      {/* ЦЕНТРАЛЬНАЯ КОЛОНКА */}
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

      {/* ПРАВАЯ КОЛОНКА */}
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
  );
}
