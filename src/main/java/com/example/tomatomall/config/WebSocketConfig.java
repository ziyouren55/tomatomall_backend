package com.example.tomatomall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.tomatomall.util.TokenUtil;
import com.example.tomatomall.config.TokenHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 限制允许的前端 origin，避免浏览器因 credentials 模式下 Access-Control-Allow-Origin 为 '*' 而拒绝
        // 使用自定义 HandshakeHandler 从 token 中解析 Principal
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .setHandshakeHandler(new TokenHandshakeHandler(tokenUtil))
                .withSockJS();
        // also support /api/ws in case frontend base URL includes /api prefix
        registry.addEndpoint("/api/ws")
                .setAllowedOriginPatterns("http://localhost:5173")
                .setHandshakeHandler(new TokenHandshakeHandler(tokenUtil))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}



