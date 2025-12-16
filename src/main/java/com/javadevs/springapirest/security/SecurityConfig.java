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


@Configuration
//Le indica al contenedor de spring que esta es una clase de seguridad al momento de arrancar la aplicación
@EnableWebSecurity
//Indicamos que se activa la seguridad web en nuestra aplicación y además esta será una clase la cual contendrá toda la configuración referente a la seguridad

public class SecurityConfig {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    //Este bean va a encargarse de verificar la información de los usuarios que se loguearán en nuestra api
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //Con este bean nos encargaremos de encriptar todas nuestras contraseñas
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }





    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    // Permite todas las origenes locales comunes
                    config.addAllowedOrigin("http://localhost:5500");
                    config.addAllowedOrigin("http://localhost");
                    return config;
                }))

                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        //Permitimos el manejo de excepciones
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) //Nos establece un punto de entrada personalizado de autenticación para el manejo de autenticaciones no autorizadas
                )
                .sessionManagement(sessionManagement -> sessionManagement  //permite la gestion de sessiones
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/auth/**").permitAll()
                        //toda peticion http debe ser autorizada
                        .requestMatchers(HttpMethod.POST, "/api/pelicula/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/pelicula/listar").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/pelicula/listarId/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/pelicula/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/pelicula/actualizar").hasAuthority("ADMIN")
                        //salas
                        .requestMatchers(HttpMethod.POST, "/api/sala/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sala/listar").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/sala/listarId/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/sala/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/sala/actualizar").hasAuthority("ADMIN")

                        // Endpoints de Asiento
                        .requestMatchers(HttpMethod.POST, "/api/asiento/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/listar").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/listarId/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/por-sala/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/asiento/disponibles/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/asiento/actualizar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/asiento/eliminar/**").hasAuthority("ADMIN")


                            //sesiones
                        .requestMatchers(HttpMethod.POST, "/api/sesiones/crear").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/listar").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/listarId/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/sesiones/actualizar").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/sesiones/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/pelicula/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/sala/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/fecha/**").hasAnyAuthority("ADMIN", "USER")
                        //.requestMatchers(HttpMethod.GET, "/api/sesiones/fecha/**/pelicula/**").hasAnyAuthority("ADMIN", "USER")

                        //boletos
                        .requestMatchers(HttpMethod.POST, "/api/boletos/crear").hasAnyAuthority("ADMIN", "USER")
                        //.requestMatchers(HttpMethod.GET, "/api/boletos/listar").hasAnyAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/listar").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/boletos/listarId/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/boletos/actualizar").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/boletos/eliminar/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/boletos/cancelar/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/sesion/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/estado/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/fecha/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/boletos/asientos-ocupados/**").hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {});
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
