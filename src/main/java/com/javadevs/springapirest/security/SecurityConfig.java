package com.javadevs.springapirest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost",
                "http://192.168.1.67:8080",
                "http://192.168.1.67",
                "http://10.0.2.2:8080",
                "http://10.0.2.2"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers",
                "Access-Control-Allow-Origin",
                "Sec-WebSocket-Protocol",
                "Sec-WebSocket-Version",
                "Sec-WebSocket-Key",
                "Upgrade",                 // CRÍTICO para WebSocket
                "Connection",              // CRÍTICO para WebSocket
                "Stomp-Destination",       // Para STOMP
                "Message-ID",              // Para STOMP
                "Subscription",            // Para STOMP
                "token"                    // Para nuestro token personalizado
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization",
                "Sec-WebSocket-Accept"     // Para WebSocket
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        // IMPORTANTE: Esto es necesario para WebSocket
        configuration.addAllowedOriginPattern("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())  // IMPORTANTE: Deshabilitar CSRF para WebSocket
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // WebSocket endpoints - PERMITIR handshake inicial
                        .requestMatchers("/ws-chat/**").permitAll() // El handshake debe ser público
                        .requestMatchers("/ws-chat").permitAll()

                        // Chat endpoints - requerir autenticación
                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers("/api/admin/chat/**").hasAuthority("ADMIN")

                        // ADMIN endpoints
                        .requestMatchers(HttpMethod.POST, "/api/pelicula/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/pelicula/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pelicula/actualizar").hasAuthority("ADMIN")

                        // Lecturas públicas
                        .requestMatchers(HttpMethod.GET, "/api/pelicula/listar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pelicula/listarId/**").permitAll()

                        // Salas
                        .requestMatchers(HttpMethod.POST, "/api/sala/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sala/listar").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/sala/listarId/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.DELETE, "/api/sala/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/sala/actualizar").hasAuthority("ADMIN")

                        // Asientos
                        .requestMatchers(HttpMethod.POST, "/api/asiento/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/listar").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/listarId/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/porsala/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/asiento/disponibles/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.PUT, "/api/asiento/actualizar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/asiento/eliminar/**").hasAuthority("ADMIN")

                        // Sesiones
                        .requestMatchers(HttpMethod.POST, "/api/sesiones/crear").hasAnyAuthority("ADMIN", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/listar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/listarId/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/sesiones/actualizar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sesiones/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/pelicula/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/sala/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/fecha/**").hasAnyAuthority("ADMIN", "USER", "GUEST")

                        // Boletos
                        .requestMatchers(HttpMethod.POST, "/api/boletos/crear").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/listar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/listarId/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.PUT, "/api/boletos/actualizar").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.DELETE, "/api/boletos/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/boletos/cancelar/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/sesion/**").hasAnyAuthority("ADMIN", "USER", "GUEST")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/estado/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/fecha/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/asientos-ocupados/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/boletos/mis-boletos/**").hasAnyAuthority("ADMIN", "USER", "GUEST")

                        // Endpoint para obtener información del usuario
                        .requestMatchers(HttpMethod.GET, "/api/auth/info-usuario").hasAnyAuthority("ADMIN", "USER", "GUEST")

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}