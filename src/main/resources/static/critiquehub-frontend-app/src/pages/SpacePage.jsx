import React, { useState, useEffect, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import MessageItem from "../components/MessageItem";
import "./SpacePage.css";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function SpacePage() {
  const { spaceId } = useParams();
  const { user } = useAuth();
  const userId = user?.id || user?.userId; // Получаем ID авторизованного юзера

  const [spaceInfo, setSpaceInfo] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [favorites, setFavorites] = useState([]); // Стейт для Избранного

  const messagesBoxRef = useRef(null);

  const forceScrollToBottom = () => {
    const container = messagesBoxRef.current;
    if (container) container.scrollTop = container.scrollHeight;
  };

  // 1. Загрузка данных (История + Избранное по твоему GET эндпоинту)
  useEffect(() => {
    const fetchMessages = () => {
      apiClient.get(`/messages/space/${spaceId}`)
        .then(res => {
          const sortedMessages = [...res.data].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
          setMessages(prev => {
            if (prev.length === sortedMessages.length) return prev;
            const container = messagesBoxRef.current;
            if (container) {
              const distanceFromBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
              if (distanceFromBottom > 200 && prev.length > 0) return sortedMessages;
            }
            setTimeout(forceScrollToBottom, 30);
            return sortedMessages;
          });
        })
        .catch(err => console.error("Ошибка обновления сообщений:", err));
    };

    // Загрузка инфо о пространстве
    apiClient.get(`/spaces/${spaceId}`)
      .then(res => setSpaceInfo(res.data))
      .catch(err => console.error("Ошибка загрузки пространства:", err));

    // ТВОЙ ЭНДПОИНТ: Получение избранного
    if (userId) {
      apiClient.get(`/users/${userId}/favorites`)
        .then(res => setFavorites(res.data))
        .catch(err => console.error("Ошибка загрузки избранного:", err));
    }

    fetchMessages();
    setLoading(false);

    const interval = setInterval(fetchMessages, 3000);
    return () => clearInterval(interval);
  }, [spaceId, userId]);

  // 2. Отправка сообщения
  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || sending) return;

    setSending(true);
    const textToSend = newMessage;
    setNewMessage("");

    try {
      const response = await apiClient.post("/messages", {
        text: textToSend,
        userId: Number(userId),
        spaceId: Number(spaceId)
      });
      setMessages(prev => [...prev, response.data]);
      setTimeout(forceScrollToBottom, 10);
    } catch (err) {
      console.error("Ошибка отправки:", err);
      setNewMessage(textToSend);
    } finally {
      setSending(false);
    }
  };

  // Проверка: добавлена ли текущая комната в избранное
  const isCurrentSpaceFavorite = favorites.some(fav => Number(fav.id) === Number(spaceId));

  // 3. ТВОИ ЭНДПОИНТЫ: Добавление (POST) и Удаление (DELETE)
  const handleToggleFavorite = async () => {
    if (!userId) return;
    try {
      if (isCurrentSpaceFavorite) {
        await apiClient.delete(`/users/${userId}/favorites/${spaceId}`);
        setFavorites(prev => prev.filter(fav => Number(fav.id) !== Number(spaceId)));
      } else {
        await apiClient.post(`/users/${userId}/favorites/${spaceId}`);
        setFavorites(prev => [...prev, { id: spaceId, name: spaceInfo?.name || `Пространство #${spaceId}` }]);
      }
    } catch (err) {
      console.error("Ошибка изменения статуса избранного:", err);
      alert("Не удалось обновить статус избранного.");
    }
  };

  if (loading) return <div className="space-loading">Загрузка чата...</div>;

  return (
      <div className="space-container">
        {/* Шапку отсюда МЫ УБРАЛИ, теперь здесь сразу начинается тело */}
        <div className="space-main-body">

          {/* Левая колонка — список избранного */}
          <aside className="sidebar">
            <h3 className="sidebar-title">ИЗБРАННОЕ</h3>
            <div className="favorites-content">
               {!user ? (
                  <p className="favorites-empty-text">Чтобы увидеть избранное, <Link to="/login" className="login-link">войдите</Link>.</p>
               ) : favorites.length > 0 ? (
                  <ul className="tags-list">
                    {favorites.map(fav => (
                      <li key={fav.id} className={`fav-item-sidebar ${Number(fav.id) === Number(spaceId) ? 'active-fav' : ''}`}>
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

          {/* Правая часть — шапка, сообщения и ввод (ВСЁ ТЕПЕРЬ ТУТ) */}
          <div className="chat-area">

            {/* ПЕРЕНЕСЛИ СЮДА: Шапка теперь закреплена строго над чатом */}
            <div className="space-header">
              <div className="space-title-row">
                <h2>{spaceInfo?.name || `Пространство #${spaceId}`}</h2>
                {user && (
                  <button
                    className={`favorite-toggle-btn ${isCurrentSpaceFavorite ? 'is-fav' : ''}`}
                    onClick={handleToggleFavorite}
                    title={isCurrentSpaceFavorite ? "Убрать из избранного" : "Добавить в избранное"}
                  >
                    {isCurrentSpaceFavorite ? "★" : "＋"}
                  </button>
                )}
              </div>
              <p>{spaceInfo?.description || "Описание отсутствует"}</p>
            </div>

            {/* Область сообщений */}
            <div className="messages-box" ref={messagesBoxRef}>
              {messages.length === 0 ? (
                <div className="no-messages">Здесь пока нет сообщений. Начните общение первым!</div>
              ) : (
                messages.map((msg) => (
                  <MessageItem key={msg.id ? `id-${msg.id}` : `rand-${Math.random()}`} msg={msg} currentUser={user} />
                ))
              )}
            </div>

            {/* Поле ввода */}
            <div className="space-footer">
              {user ? (
                <form className="message-form" onSubmit={handleSendMessage}>
                  <input
                    type="text"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder="Напишите сообщение..."
                    maxLength={1000}
                    disabled={sending}
                  />
                  <button type="submit" disabled={sending || !newMessage.trim()}>{sending ? "..." : "Отправить"}</button>
                </form>
              ) : (
                <div className="auth-reminder">Чтобы участвовать в обсуждении, <Link to="/login">войдите</Link>.</div>
              )}
            </div>

          </div>
        </div>
      </div>
    );
}
