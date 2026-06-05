package com.critiquehub.util.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@RequiredArgsConstructor
public class SpaceWebSocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(SpaceWebSocketHandler.class);
    private final Map<String, Set<WebSocketSession>> spaceSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) {
        try {
            final String spaceId = extractSpaceId(session);
            spaceSessions.computeIfAbsent(spaceId, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("WebSocket connected. SpaceId: {}, SessionId: {}", spaceId, session.getId());
        } catch (IllegalArgumentException e) {
            log.error("Connection rejected: {}", e.getMessage());
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (IOException ioException) {
                log.error("Failed to close session", ioException);
            }
        }
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) {
        spaceSessions.values().forEach(sessions -> sessions.remove(session));
        spaceSessions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        log.info("WebSocket disconnected. SessionId: {}, Status: {}", session.getId(), status);
    }

    @Override
    public void handleTransportError(final WebSocketSession session, final Throwable exception) {
        log.error("WebSocket transport error for session: " + session.getId(), exception);
    }

    public void broadcastToSpace(final String spaceId, final Object messageObject) {
        final Set<WebSocketSession> sessions = spaceSessions.get(spaceId);
        if (sessions != null && !sessions.isEmpty()) {
            try {
                final String jsonPayload = objectMapper.writeValueAsString(messageObject);
                final TextMessage textMessage = new TextMessage(jsonPayload);

                for (final WebSocketSession session : sessions) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
            } catch (IOException e) {
                log.error("WebSocket conversion error", e);
            }
        }
    }

    private String extractSpaceId(final org.springframework.web.socket.WebSocketSession session) {
        final java.net.URI uri = session.getUri();
        if (uri != null && uri.getQuery() != null) {
            final String query = uri.getQuery();
            for (String param : query.split("&")) {
                final String[] pair = param.split("=");
                if (pair.length > 1 && "spaceId".equals(pair[0])) {
                    return pair[1];
                }
            }
        }
        throw new IllegalArgumentException("Missing spaceId parameter in WebSocket connection");
    }
}
