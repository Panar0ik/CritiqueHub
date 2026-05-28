import React, { useState, useEffect, useRef } from "react";
import axios from "axios";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function MessageItem({ msg, currentUser, onReply, onEdit, onDelete }) {
  const [senderName, setSenderName] = useState(msg.authorName || "Загрузка...");

  const [showMenu, setShowMenu] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    if (msg.authorName) {
      setSenderName(msg.authorName);
      return;
    }

    if (msg.userId) {
      apiClient.get(`/users/${msg.userId}`)
        .then((res) => {
          setSenderName(res.data.username || `Пользователь #${msg.userId}`);
        })
        .catch(() => {
          setSenderName(`Пользователь #${msg.userId}`);
        });
    } else {
      setSenderName("Участник");
    }
  }, [msg]);

  useEffect(() => {
    function handleClickOutside(event) {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowMenu(false);
      }
    }
    if (showMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showMenu]);

  const isMyMessage =
    msg.authorName === currentUser?.username ||
    Number(msg.userId) === Number(currentUser?.id || currentUser?.userId);

  const formatTime = (timestampString) => {
    if (!timestampString) return "";
    const date = new Date(timestampString);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className={`message-item ${isMyMessage ? "my-message" : ""}`}>
      <div className="message-main-content">
        <div className="message-sender">{senderName}</div>
        <div className="message-content">{msg.text}</div>

        {msg.attachments && msg.attachments.length > 0 && (
          <div className="message-attachments">
            {msg.attachments.map((attach) => {
              if (!attach.url) return null;

              const isProbablyImage = attach.url.match(/\.(jpeg|jpg|gif|png|webp)/i) || attach.url.includes("images");

              return (
                <div key={attach.id} className="attachment-preview">
                  {isProbablyImage ? (
                    <img
                      src={attach.url}
                      alt="Вложение"
                      className="attached-img"
                      onError={(e) => {
                        e.target.style.display = 'none';
                        const link = e.target.nextSibling;
                        if (link) link.style.display = 'inline-block';
                      }}
                    />
                  ) : null}

                  <a
                    href={attach.url}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="attached-link"
                    style={{ display: isProbablyImage ? 'none' : 'inline-block' }}
                  >
                    📎 {isProbablyImage ? "Открыть защищенное изображение" : "Вложенный файл"}
                  </a>
                </div>
              );
            })}
          </div>
        )}

        <div className="message-time">{formatTime(msg.timestamp)}</div>
      </div>

      {currentUser && (
        <div className="message-menu-wrapper" ref={menuRef}>
          <button type="button" className="three-dots-btn" onClick={() => setShowMenu(!showMenu)}>
            ⋮
          </button>

          {showMenu && (
            <div className="message-dropdown-menu">
              <button type="button" onClick={() => { onReply(msg); setShowMenu(false); }}>
                Ответить
              </button>

              {isMyMessage && (
                <>
                  <button type="button" onClick={() => { onEdit(msg); setShowMenu(false); }}>
                    Изменить
                  </button>
                  <button type="button" className="delete-menu-item" onClick={() => { onDelete(msg.id); setShowMenu(false); }}>
                    Удалить
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
