package com.javadevs.springapirest.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtGenerador {

    // Clave segura generada automáticamente
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // Método para crear un token por medio de la authentication
    public String generarToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Obtener roles del usuario
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Tomar el primer rol (o adaptar según tu lógica)
        String role = roles.isEmpty() ? "USER" : roles.get(0);

        Date tiempoActual = new Date();
        Date expiracionToken = new Date(tiempoActual.getTime() + ConstantesSeguridad.JWT_EXPIRATION_TOKEN);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(expiracionToken)
                .signWith(secretKey, SignatureAlgorithm.HS512) // Usar la clave segura
                .compact();
    }

    // Método para extraer un Username a partir de un token
    public String obtenerUsernameDeJwt(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // Método para validar el token
    public Boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Jwt ha expirado o esta incorrecto");
        }
    }

    // Método para obtener todos los claims del token
    public Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método para obtener un claim específico
    public String obtenerClaim(String token, String claimName) {
        Claims claims = obtenerClaims(token);
        return claims.get(claimName, String.class);
    }

    // Método para verificar si el token contiene un claim específico
    public boolean tieneClaim(String token, String claimName) {
        try {
            Claims claims = obtenerClaims(token);
            return claims.get(claimName) != null;
        } catch (Exception e) {
            return false;
        }
    }
}