import React, { useState, useEffect } from "react";
import axios from "axios";

const apiClient = axios.create({ baseURL: "http://localhost:8080/api" });

export default function MessageItem({ msg, currentUser }) {
  const [senderName, setSenderName] = useState(msg.authorName || "Загрузка...");

  useEffect(() => {
    // Если имя автора уже вшито в объект сообщения (например, при POST), сразу берем его
    if (msg.authorName) {
      setSenderName(msg.authorName);
      return;
    }

    // Если имени нет, но есть userId — запрашиваем его у твоего API
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

  // Проверяем, наше ли это сообщение
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
      <div className="message-sender">{senderName}</div>
      <div className="message-content">{msg.text}</div>
      <div className="message-time">{formatTime(msg.timestamp)}</div>
    </div>
  );
}
