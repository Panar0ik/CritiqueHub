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
  const userId = user?.id || user?.userId;

  const [spaceInfo, setSpaceInfo] = useState(null);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [favorites, setFavorites] = useState([]);

  const [editingMessage, setEditingMessage] = useState(null);
  const [replyToMessage, setReplyToMessage] = useState(null);
  const [attachmentUrl, setAttachmentUrl] = useState("");

  const messagesBoxRef = useRef(null);

  const forceScrollToBottom = () => {
    const container = messagesBoxRef.current;
    if (container) container.scrollTop = container.scrollHeight;
  };

useEffect(() => {
  if (!spaceId) return;

  const fetchMessages = async () => {
    try {
      const res = await apiClient.get(`/messages/space/${spaceId}`);
      const sorted = [...res.data].sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));

      const messagesWithAttachments = await Promise.all(
        sorted.map(async (msg) => {
          try {
            const attachRes = await apiClient.get(`/attachments/message/${msg.id}`);
            return { ...msg, attachments: attachRes.data || [] };
          } catch {
            return { ...msg, attachments: [] };
          }
        })
      );

      setMessages(prev => {
        if (JSON.stringify(prev) === JSON.stringify(messagesWithAttachments)) return prev;

        const container = messagesBoxRef.current;
        if (container) {
          const distanceFromBottom = container.scrollHeight - container.scrollTop - container.clientHeight;
          if (distanceFromBottom > 200 && prev.length > 0) return messagesWithAttachments;
        }
        setTimeout(forceScrollToBottom, 30);
        return messagesWithAttachments;
      });
    } catch (err) {
      console.error("Ошибка обновления сообщений:", err);
    }
  };

  apiClient.get(`/spaces/${spaceId}`)
    .then(res => setSpaceInfo(res.data))
    .catch(err => console.error("Ошибка загрузки пространства:", err));

  if (userId) {
    apiClient.get(`/users/${userId}/favorites`)
      .then(res => setFavorites(res.data))
      .catch(err => console.error("Ошибка загрузки избранного:", err));
  }

  fetchMessages();
  setLoading(false);

  let ws = null;
  const connectTimer = setTimeout(() => {
    ws = new WebSocket(`ws://localhost:8080/api/ws/spaces?spaceId=${spaceId}`);

    ws.onmessage = (event) => {
      const socketData = JSON.parse(event.data);

      if (socketData.type === "UPDATE_MESSAGES") {
        fetchMessages();
      } else if (socketData.type === "NEW_MESSAGE") {
        fetchMessages();
      }
    };
  }, 100);

  return () => {
    clearTimeout(connectTimer);
    if (ws && (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING)) {
      ws.close();
    }
  };
}, [spaceId, userId]);

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!newMessage.trim() || sending) return;

    setSending(true);
    const textToSend = newMessage;
    const currentAttachment = attachmentUrl.trim();

    setNewMessage("");
    setAttachmentUrl("");
    setReplyToMessage(null);

    try {
      if (editingMessage) {
        await apiClient.put(`/messages/${editingMessage.id}?spaceId=${spaceId}`, JSON.stringify(textToSend), {
          headers: { "Content-Type": "application/json" }
        });

        setMessages(prev => prev.map(msg =>
          msg.id === editingMessage.id ? { ...msg, text: textToSend } : msg
        ));
        setEditingMessage(null);
      } else {
        const finalText = replyToMessage
          ? `↪ Отвечая на ${replyToMessage.authorName || 'участника'}: "${replyToMessage.text.slice(0, 30)}..."\n${textToSend}`
          : textToSend;

        const response = await apiClient.post("/messages", {
          text: finalText,
          userId: Number(userId),
          spaceId: Number(spaceId)
        });

        let createdMessage = response.data;
        createdMessage.attachments = [];

        if (currentAttachment) {
          try {
            const attachResponse = await apiClient.post("/attachments", {
              url: currentAttachment,
              messageId: Number(createdMessage.id)
            });
            createdMessage.attachments.push(attachResponse.data);
          } catch (attachErr) {
            console.error("Не удалось сохранить вложение:", attachErr);
          }
        }

        setMessages(prev => [...prev, createdMessage]);
        setTimeout(forceScrollToBottom, 10);
      }
    } catch (err) {
      console.error("Ошибка отправки действия:", err);
      setNewMessage(textToSend);
    } finally {
      setSending(false);
    }
  };

  const handleDeleteMessage = async (messageId) => {
    if (!window.confirm("Вы уверены, что хотите удалить это сообщение?")) return;
    try {
      await apiClient.delete(`/messages/${messageId}?spaceId=${spaceId}`);
      setMessages(prev => prev.filter(msg => msg.id !== messageId));
    } catch (err) {
      console.error("Не удалось удалить сообщение:", err);
    }
  };

  const handleStartEdit = (msg) => {
    setReplyToMessage(null);
    setEditingMessage({ id: msg.id, text: msg.text });
    setNewMessage(msg.text);
  };

  const handleStartReply = (msg) => {
    setEditingMessage(null);
    setReplyToMessage(msg);
  };

  const isCurrentSpaceFavorite = favorites.some(fav => Number(fav.id) === Number(spaceId));

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
    }
  };

  if (loading) return <div className="space-loading">Загрузка чата...</div>;

  return (
    <div className="space-container">
      <div className="space-main-body">
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

        <div className="chat-area">
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

          <div className="messages-box" ref={messagesBoxRef}>
            {messages.length === 0 ? (
              <div className="no-messages">Здесь пока нет сообщений. Начните общение первым!</div>
            ) : (
              messages.map((msg) => (
                <MessageItem
                  key={msg.id}
                  msg={msg}
                  currentUser={user}
                  onDelete={handleDeleteMessage}
                  onEdit={handleStartEdit}
                  onReply={handleStartReply}
                />
              ))
            )}
          </div>

          <div className="space-footer">
            {user ? (
              <form className="message-form" onSubmit={handleSendMessage}>

                {editingMessage && (
                  <div className="input-mode-panel edit-mode">
                    <span>Редактирование сообщения</span>
                    <button type="button" onClick={() => { setEditingMessage(null); setNewMessage(""); }}>✕</button>
                  </div>
                )}

                {replyToMessage && (
                  <div className="input-mode-panel reply-mode">
                    <span>Ответ пользователю <strong>{replyToMessage.authorName || "Участник"}</strong></span>
                    <button type="button" onClick={() => setReplyToMessage(null)}>✕</button>
                  </div>
                )}

                <div className="input-row-container">

                  {!editingMessage && (
                    <div className="attachment-wrapper">
                      <button
                        type="button"
                        className={`attach-toggle-btn ${attachmentUrl.trim() ? "has-attachment" : ""}`}
                        onClick={() => {
                          const url = prompt("Введите URL ссылку на картинку или файл:", attachmentUrl);
                          if (url !== null) setAttachmentUrl(url);
                        }}
                        title="Прикрепить файл по URL"
                      >
                        📎
                      </button>
                    </div>
                  )}

                  <input
                    type="text"
                    className="message-main-input"
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    placeholder={editingMessage ? "Измените сообщение..." : "Напишите сообщение..."}
                    maxLength={1000}
                    disabled={sending}
                  />
                  <button type="submit" className="send-btn" disabled={sending || !newMessage.trim()}>
                    {sending ? "..." : editingMessage ? "Сохранить" : "Отправить"}
                  </button>
                </div>

                {attachmentUrl.trim() && !editingMessage && (
                  <div className="attachment-badge">
                    <span>📎 Файл прикреплен ({attachmentUrl.slice(0, 35)}...)</span>
                    <button type="button" onClick={() => setAttachmentUrl("")}>✕</button>
                  </div>
                )}

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
