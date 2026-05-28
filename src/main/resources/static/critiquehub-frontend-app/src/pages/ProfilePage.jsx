import React, { useState, useEffect } from "react";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { useNavigate, Link } from "react-router-dom";
import "./Profile.css"

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function ProfilePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const userId = user?.id || user?.userId;

  const [mySpaces, setMySpaces] = useState([]);
  const [favorites, setFavorites] = useState([]);

  const [activeTab, setActiveTab] = useState("spaces");

  const [spaceForm, setSpaceForm] = useState({ name: "", description: "", tagNames: "" });
  const [editingSpaceId, setEditingSpaceId] = useState(null);
  const [profileForm, setProfileForm] = useState({
    username: user?.username || "",
    email: user?.email || "",
    password: ""
  });

  const [deleteModal, setDeleteModal] = useState({ isOpen: false, type: null, id: null, title: "" });

  useEffect(() => {
    if (!userId) {
      navigate("/login");
      return;
    }
    fetchMySpaces();
    fetchFavorites();
  }, [userId]);

  const fetchMySpaces = () => {
      apiClient.get(`/spaces/owner/${userId}`)
        .then(res => {
          const results = res.data.content || res.data || [];
          setMySpaces(results);
        })
        .catch(err => {
          console.error("Ошибка загрузки пространств пользователя:", err);
          setMySpaces([]);
        });
    };

  const fetchFavorites = () => {
    apiClient.get(`/users/${userId}/favorites`)
      .then(res => setFavorites(res.data.content || res.data || []))
      .catch(err => console.error("Ошибка загрузки избранного:", err));
  };

  const handleSpaceSubmit = (e) => {
    e.preventDefault();
    const payload = {
      name: spaceForm.name,
      description: spaceForm.description,
      ownerId: userId,
      tagNames: spaceForm.tagNames.split(",").map(t => t.trim()).filter(Boolean)
    };

    if (editingSpaceId) {
      apiClient.put(`/spaces/${editingSpaceId}`, payload)
        .then(() => {
          setEditingSpaceId(null);
          setSpaceForm({ name: "", description: "", tagNames: "" });
          fetchMySpaces();
        })
        .catch(err => console.error("Ошибка обновления пространства:", err));
    } else {
      apiClient.post(`/spaces`, payload)
        .then(() => {
          setSpaceForm({ name: "", description: "", tagNames: "" });
          fetchMySpaces();
        })
        .catch(err => console.error("Ошибка создания пространства:", err));
    }
  };

  const handleEditClick = (space) => {
    setEditingSpaceId(space.id);
    setSpaceForm({
      name: space.name,
      description: space.description || "",
      tagNames: (space.tagNames || []).join(", ")
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const executeDeleteSpace = (id) => {
    apiClient.delete(`/spaces/${id}`)
      .then(() => {
        fetchMySpaces();
        setDeleteModal({ isOpen: false, type: null, id: null, title: "" });
      })
      .catch(err => console.error("Ошибка удаления пространства:", err));
  };

  const handleRemoveFavorite = (spaceId) => {
    apiClient.delete(`/users/${userId}/favorites/${spaceId}`)
      .then(() => fetchFavorites())
      .catch(err => console.error("Ошибка удаления из избранного:", err));
  };

  const handleProfileSubmit = (e) => {
    e.preventDefault();
    apiClient.put(`/users/${userId}`, profileForm)
      .then(() => {
        alert("Профиль успешно обновлен! Пожалуйста, войдите заново.");
        logout();
        navigate("/login");
      })
      .catch(err => console.error("Ошибка обновления профиля:", err));
  };

  const executeDeleteAccount = () => {
    apiClient.delete(`/users/${userId}`)
      .then(() => {
        logout();
        navigate("/");
      })
      .catch(err => console.error("Ошибка удаления аккаунта:", err));
  };

  const openDeleteModal = (type, id, title) => {
    setDeleteModal({ isOpen: true, type, id, title });
  };

  const confirmDelete = () => {
    if (deleteModal.type === "space") {
      executeDeleteSpace(deleteModal.id);
    } else if (deleteModal.type === "account") {
      executeDeleteAccount();
    }
  };

  return (
    <div className="profile-container">
      <h1 className="profile-title">Личный кабинет</h1>

      {/* Навигация по вкладкам */}
      <div className="profile-tabs">
        <button className={activeTab === "spaces" ? "active" : ""} onClick={() => setActiveTab("spaces")}>
          Мои пространства
        </button>
        <button className={activeTab === "favorites" ? "active" : ""} onClick={() => setActiveTab("favorites")}>
          Избранное
        </button>
        <button className={activeTab === "settings" ? "active" : ""} onClick={() => setActiveTab("settings")}>
          Настройки аккаунта
        </button>
      </div>

      <div className="profile-content">

        {activeTab === "spaces" && (
          <div className="tab-section">
            <h2>{editingSpaceId ? "Редактировать пространство" : "Создать новое пространство"}</h2>
            <form onSubmit={handleSpaceSubmit} className="profile-form">
              <input
                type="text"
                placeholder="Название пространства"
                value={spaceForm.name}
                onChange={e => setSpaceForm({...spaceForm, name: e.target.value})}
                required
              />
              <textarea
                placeholder="Описание"
                value={spaceForm.description}
                onChange={e => setSpaceForm({...spaceForm, description: e.target.value})}
              />
              <input
                type="text"
                placeholder="Теги (через запятую, например: java, react)"
                value={spaceForm.tagNames}
                onChange={e => setSpaceForm({...spaceForm, tagNames: e.target.value})}
              />
              <div className="form-actions">
                <button type="submit" className="btn-primary">
                  {editingSpaceId ? "Сохранить изменения" : "Создать"}
                </button>
                {editingSpaceId && (
                  <button type="button" className="btn-secondary" onClick={() => {
                    setEditingSpaceId(null);
                    setSpaceForm({ name: "", description: "", tagNames: "" });
                  }}>Отмена</button>
                )}
              </div>
            </form>

            <h3 className="section-subtitle">Ваши пространства ({mySpaces.length})</h3>
            <ul className="profile-list">
              {mySpaces.map(space => (
                <li key={space.id} className="profile-list-item">
                  <div>
                    <strong><Link to={`/spaces/${space.id}`}>{space.name}</Link></strong>
                    <p>{space.description}</p>
                  </div>
                  <div className="item-actions">
                    <button onClick={() => handleEditClick(space)} className="btn-edit">Изменить</button>
                    <button onClick={() => openDeleteModal("space", space.id, `пространство "${space.name}"`)} className="btn-delete">Удалить</button>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        )}

        {activeTab === "favorites" && (
          <div className="tab-section">
            <h2>Избранные пространства</h2>
            {favorites.length === 0 ? <p>Список избранного пуст.</p> : (
              <ul className="profile-list">
                {favorites.map(fav => (
                  <li key={fav.id} className="profile-list-item">
                    <strong><Link to={`/spaces/${fav.id}`}>{fav.name}</Link></strong>
                    <button onClick={() => handleRemoveFavorite(fav.id)} className="btn-warning">Убрать из избранного</button>
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}

        {activeTab === "settings" && (
          <div className="tab-section">
            <h2>Редактирование профиля</h2>
            <form onSubmit={handleProfileSubmit} className="profile-form">
              <input
                type="text"
                placeholder="Имя пользователя"
                value={profileForm.username}
                onChange={e => setProfileForm({...profileForm, username: e.target.value})}
                required
              />
              <input
                type="email"
                placeholder="Email"
                value={profileForm.email}
                onChange={e => setProfileForm({...profileForm, email: e.target.value})}
                required
              />
              <input
                type="password"
                placeholder="Новый пароль (оставьте пустым, если не меняете)"
                value={profileForm.password}
                onChange={e => setProfileForm({...profileForm, password: e.target.value})}
              />
              <button type="submit" className="btn-primary">Обновить данные</button>
            </form>

            <div className="danger-zone">
              <h3>Опасная зона</h3>
              <p>Удаление аккаунта приведет к безвозвратной потере всех ваших данных.</p>
              <button onClick={() => openDeleteModal("account", userId, "свой аккаунт безвозвратно")} className="btn-delete">
                Удалить аккаунт
              </button>
            </div>
          </div>
        )}

      </div>

      {deleteModal.isOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Подтверждение действия</h3>
            <p>Вы уверены, что хотите удалить {deleteModal.title}?</p>
            <div className="modal-actions">
              <button onClick={() => setDeleteModal({ isOpen: false, type: null, id: null, title: "" })} className="btn-secondary">Отмена</button>
              <button onClick={confirmDelete} className="btn-delete">Да, удалить</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
