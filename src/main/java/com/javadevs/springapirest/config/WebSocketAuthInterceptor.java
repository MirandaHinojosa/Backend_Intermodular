// Archivo: WebSocketAuthInterceptor.java
package com.javadevs.springapirest.config;

import com.javadevs.springapirest.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtGenerador jwtGenerador;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Obtener el token de los headers
            String token = getTokenFromHeaders(accessor);

            if (token != null && jwtGenerador.validarToken(token)) {
                try {
                    String username = jwtGenerador.obtenerUsernameDeJwt(token);

                    // Obtener el rol del token
                    String role = getRoleFromToken(token);

                    // Crear autenticación
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    List.of(new SimpleGrantedAuthority(role))
                            );

                    // Establecer autenticación en el contexto
                    accessor.setUser(auth);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception e) {
                    // Token inválido - rechazar conexión
                    return null;
                }
            } else {
                // Sin token o token inválido - rechazar conexión
                return null;
            }
        }

        return message;
    }

    private String getTokenFromHeaders(StompHeaderAccessor accessor) {
        // Primero buscar en los headers nativos
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }

        // También buscar en los atributos de la sesión (para SockJS)
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null && sessionAttributes.containsKey("token")) {
            return (String) sessionAttributes.get("token");
        }

        return null;
    }

    private String getRoleFromToken(String token) {
        try {
            // Extraer el claim "role" del token

            io.jsonwebtoken.Claims claims = io.jsonwebtoken.Jwts.parser()
                    .setSigningKey("firma")
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("role", String.class);
        } catch (Exception e) {
            return "USER"; // Rol por defecto
        }
    }
}