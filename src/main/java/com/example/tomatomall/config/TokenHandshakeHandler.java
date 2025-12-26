package com.example.tomatomall.config;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.util.TokenUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * 在 WebSocket 握手阶段解析 token，并将对应的 Account id 作为 Principal 的 name
 * 这样后端使用 convertAndSendToUser(userId, ...) 时能正确路由到该用户的会话
 */
public class TokenHandshakeHandler extends DefaultHandshakeHandler {

    private final TokenUtil tokenUtil;

    public TokenHandshakeHandler(TokenUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected Principal determineUser(@org.springframework.lang.NonNull ServerHttpRequest request,
                                     @org.springframework.lang.NonNull WebSocketHandler wsHandler,
                                     @org.springframework.lang.NonNull Map<String, Object> attributes) {
        try {
            // 尝试从 header 中取 token
            List<String> tokens = request.getHeaders().get("token");
            String token = null;
            if (tokens != null && !tokens.isEmpty()) {
                token = tokens.get(0);
            } else {
                // 尝试从查询参数中读取 ?token=...
                URI uri = request.getURI();
                if (uri != null && uri.getQuery() != null) {
                    String[] parts = uri.getQuery().split("&");
                    for (String p : parts) {
                        if (p.startsWith("token=")) {
                            token = p.substring(6);
                            break;
                        }
                    }
                }
            }
            if (token != null && tokenUtil.verifyToken(token)) {
                Account account = tokenUtil.getAccount(token);
                final String name = String.valueOf(account.getId());
                return new Principal() {
                    @Override
                    public String getName() {
                        return name;
                    }
                };
            }
        } catch (Exception e) {
            // ignore and fallthrough to default
            System.out.println("TokenHandshakeHandler determineUser failed: " + e.getMessage());
        }
        return super.determineUser(request, wsHandler, attributes);
    }
}


