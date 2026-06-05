package com.critiquehub.util.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final SpaceWebSocketHandler spaceWebSocketHandler;

    public WebSocketConfig(final SpaceWebSocketHandler pSpaceWebSocketHandler) {
        this.spaceWebSocketHandler = pSpaceWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        registry.addHandler(spaceWebSocketHandler, "/api/ws/spaces")
                .setAllowedOriginPatterns("*");
    }
}
