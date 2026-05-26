import React, { useState, useEffect, useRef } from "react";
import { useParams, Link } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import MessageItem from "../components/MessageItem";
import Header from "../components/Header"; // Импортируем твой Header
import "./SpacePage.css";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function SpacePage() {
  const { spaceId } = useParams();
  const { user } = useAuth();

  const [spaceInfo, setSpaceInfo] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);

const [favorites, setFavorites] = useState([]);
  const userId = user?.id || user?.userId;
  const messagesBoxRef = useRef(null);

  // Мгновенный скролл вниз без конфликтов анимации
  const forceScrollToBottom = () => {
    const container = messagesBoxRef.current;
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  };

    useEffect(() => {
        apiClient.get("/tags").then(res => setTags(res.data.slice(0, 10)));
        apiClient.get("/spaces").then(res => setSpaces(res.data));

        const userId = user?.id || user?.userId;

            if (userId) {
              apiClient.get(`/users/${userId}/favorites`)
                .then(res => {
                  // Предполагаем, что сервер возвращает массив объектов избранного
                  setFavorites(res.data);
                })
                .catch(err => {
                  console.error("Не удалось загрузить избранное:", err);
                });
            } else {
              // Если пользователя нет, очищаем список избранного (на случай разлогина)
              setFavorites([]);
            }
      }, [user]);

  // 1. Загрузка данных чата + Polling (3 сек)
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

              // Если получатель поднялся выше 200px, не дергаем его скролл при обновлении
              if (distanceFromBottom > 200 && prev.length > 0) {
                return sortedMessages;
              }
            }

            setTimeout(forceScrollToBottom, 30);
            return sortedMessages;
          });
        })
        .catch(err => console.error("Ошибка обновления сообщений:", err));
    };

    fetchMessages();
    setLoading(false);

    const interval = setInterval(fetchMessages, 3000);
    return () => clearInterval(interval);
  }, [spaceId]);

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
        userId: Number(user?.id || user?.userId),
        spaceId: Number(spaceId)
      });

      setMessages(prev => [...prev, response.data]);
      setTimeout(forceScrollToBottom, 10);

    } catch (err) {
      console.error("Ошибка при сохранении сообщения:", err);
      setNewMessage(textToSend);
      alert("Не удалось отправить сообщение.");
    } finally {
      setSending(false);
    }
  };

  if (loading) {
    return <div className="space-loading">Загрузка чата...</div>;
  }

  return (
      <div className="space-container">
        {/* 1. Общая шапка текущего пространства */}
        <div className="space-header">
          <div className="space-title-row">
            <h2>{spaceInfo?.name || `Пространство #${spaceId}`}</h2>

            {/* КНОПКА ДОБАВЛЕНИЯ В ИЗБРАННОЕ */}
            {user && (
              <button
                className={`favorite-toggle-btn ${favorites.some(fav => Number(fav.id) === Number(spaceId)) ? 'is-fav' : ''}`}
                onClick={async () => {
                  const isAlreadyFav = favorites.some(fav => Number(fav.id) === Number(spaceId));

                  try {
                    if (isAlreadyFav) {
                      // Если уже в избранном — удаляем
                      // Твой эндпоинт может быть другим, например: delete(`/favorites/${spaceId}`)
                      await apiClient.delete(`/spaces/${spaceId}/favorite`);
                      setFavorites(prev => prev.filter(fav => Number(fav.id) !== Number(spaceId)));
                    } else {
                      // Если еще не в избранном — добавляем
                      // Передаем объект пространства, чтобы сразу обновить левую колонку
                      await apiClient.post(`/spaces/${spaceId}/favorite`);
                      setFavorites(prev => [...prev, { id: spaceId, name: spaceInfo?.name }]);
                    }
                  } catch (err) {
                    console.error("Не удалось изменить статус избранного:", err);
                    // Временный фолбек для теста на фронтенде, если бэкенд под избранное еще не написан:
                    if (isAlreadyFav) {
                      setFavorites(prev => prev.filter(fav => Number(fav.id) !== Number(spaceId)));
                    } else {
                      setFavorites(prev => [...prev, { id: spaceId, name: spaceInfo?.name || `Пространство #${spaceId}` }]);
                    }
                  }
                }}
                title={favorites.some(fav => Number(fav.id) === Number(spaceId)) ? "Убрать из избранного" : "Добавить в избранное"}
              >
                {favorites.some(fav => Number(fav.id) === Number(spaceId)) ? "★" : "＋"}
              </button>
            )}
          </div>
          <p>{spaceInfo?.description || "Описание отсутствует"}</p>
        </div>

        {/* 2. Главное тело страницы (Горизонтальный Flex) */}
        <div className="space-main-body">

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

          {/* Вертикальный разделитель */}
          <div className="vertical-line" />

          {/* ПРАВАЯ ЧАСТЬ: Сам чат */}
          <div className="chat-area">
            <div className="messages-box" ref={messagesBoxRef}>
              {messages.length === 0 ? (
                <div className="no-messages">Здесь пока нет сообщений. Начните общение первым!</div>
              ) : (
                messages.map((msg) => {
                  const messageKey = msg.id ? `id-${msg.id}` : `rand-${Math.random()}`;
                  return (
                    <MessageItem
                      key={messageKey}
                      msg={msg}
                      currentUser={user}
                    />
                  );
                })
              )}
            </div>

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
                  <button type="submit" disabled={sending || !newMessage.trim()}>
                    {sending ? "..." : "Отправить"}
                  </button>
                </form>
              ) : (
                <div className="auth-reminder">
                  Чтобы участвовать в обсуждении, пожалуйста, <Link to="/login">войдите</Link> или <Link to="/register">зарегистрируйтесь</Link>.
                </div>
              )}
            </div>
          </div>

        </div>
      </div>
    );
}
