package com.javadevs.springapirest.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;  // <-- AGREGAR ESTA IMPORT
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component  // <-- AGREGAR ESTA ANOTACIÓN
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            // Obtener token de query parameters
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token != null) {
                attributes.put("token", token);
                System.out.println("Token obtenido de query parameter: " + token);
            }

            // Obtener token de headers
            String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String bearerToken = authHeader.substring(7);
                attributes.put("token", bearerToken);
                System.out.println("Token obtenido de Authorization header: " + bearerToken);
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // No necesitamos hacer nada después del handshake
    }
}