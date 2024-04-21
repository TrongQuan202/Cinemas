package org.example.project_cinemas_java.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class EngineIoConfigurator implements WebSocketConfigurer {
    private final EngineIoHandler mEngineIoHandler;

    public EngineIoConfigurator(EngineIoHandler mEngineIoHandler) {
        this.mEngineIoHandler = mEngineIoHandler;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mEngineIoHandler, "/socket.io/")
                .addInterceptors(mEngineIoHandler)
                .setAllowedOrigins("*");
    }
}
