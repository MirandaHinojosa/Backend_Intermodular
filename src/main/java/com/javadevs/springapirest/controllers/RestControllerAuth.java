package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.dtos.DtoAuthRespuesta;
import com.javadevs.springapirest.dtos.DtoLogin;
import com.javadevs.springapirest.dtos.DtoRegistro;
import com.javadevs.springapirest.models.Roles;
import com.javadevs.springapirest.models.Usuarios;
import com.javadevs.springapirest.repositories.IRolesRepository;
import com.javadevs.springapirest.repositories.IUsuariosRepository;
import com.javadevs.springapirest.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/")
public class RestControllerAuth {

    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private IRolesRepository rolesRepository;
    private IUsuariosRepository usuariosRepository;
    private JwtGenerador jwtGenerador;

    @Autowired
    public RestControllerAuth(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, IRolesRepository rolesRepository, IUsuariosRepository usuariosRepository, JwtGenerador jwtGenerador) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.usuariosRepository = usuariosRepository;
        this.jwtGenerador = jwtGenerador;
    }

    @PostMapping("register")
    public ResponseEntity<?> registrar(@RequestBody DtoRegistro dtoRegistro) {
        // Validar que el usuario no exista
        if (usuariosRepository.existsByUsername(dtoRegistro.getUsername())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El usuario ya existe, intenta con otro");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if (dtoRegistro.getEmail() != null && !dtoRegistro.getEmail().isEmpty()) {

        }

        // Crear nuevo usuario
        Usuarios usuario = new Usuarios();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        usuario.setEmail(dtoRegistro.getEmail());
        usuario.setNombre(dtoRegistro.getNombre());

        // Asignar rol por defecto (USER)
        Roles rol = rolesRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado en la base de datos"));
        usuario.setRoles(Collections.singletonList(rol));

        usuariosRepository.save(usuario);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("mensaje", "Registro de usuario exitoso");
        successResponse.put("username", usuario.getUsername());
        successResponse.put("email", usuario.getEmail());
        successResponse.put("nombre", usuario.getNombre());

        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    @PostMapping("registerAdm")
    public ResponseEntity<?> registrarAdmin(@RequestBody DtoRegistro dtoRegistro) {
        // Validar que el usuario no exista
        if (usuariosRepository.existsByUsername(dtoRegistro.getUsername())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El usuario ya existe, intenta con otro");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Crear nuevo administrador
        Usuarios usuario = new Usuarios();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        usuario.setEmail(dtoRegistro.getEmail());
        usuario.setNombre(dtoRegistro.getNombre());

        // Asignar rol ADMIN
        Roles rol = rolesRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado en la base de datos"));
        usuario.setRoles(Collections.singletonList(rol));

        usuariosRepository.save(usuario);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("mensaje", "Registro de administrador exitoso");
        successResponse.put("username", usuario.getUsername());
        successResponse.put("email", usuario.getEmail());
        successResponse.put("nombre", usuario.getNombre());

        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    @PostMapping("registerGuest")
    public ResponseEntity<?> registrarGuest(@RequestBody DtoRegistro dtoRegistro) {
        // Validar que el usuario no exista
        if (usuariosRepository.existsByUsername(dtoRegistro.getUsername())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El usuario ya existe, intenta con otro");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        // Crear nuevo guest
        Usuarios usuario = new Usuarios();
        usuario.setUsername(dtoRegistro.getUsername());
        usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        usuario.setEmail(dtoRegistro.getEmail());
        usuario.setNombre(dtoRegistro.getNombre());

        // Asignar rol GUEST
        Roles rol = rolesRepository.findByName("GUEST")
                .orElseThrow(() -> new RuntimeException("Rol GUEST no encontrado en la base de datos"));
        usuario.setRoles(Collections.singletonList(rol));

        usuariosRepository.save(usuario);

        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("mensaje", "Registro de guest exitoso");
        successResponse.put("username", usuario.getUsername());
        successResponse.put("email", usuario.getEmail());
        successResponse.put("nombre", usuario.getNombre());

        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

      //no implementado,
    @PutMapping("actualizar-usuario")
    public ResponseEntity<?> actualizarUsuario(@RequestBody DtoRegistro dtoRegistro, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuarios usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos si se proporcionan
        if (dtoRegistro.getEmail() != null && !dtoRegistro.getEmail().isEmpty()) {
            usuario.setEmail(dtoRegistro.getEmail());
        }

        if (dtoRegistro.getNombre() != null && !dtoRegistro.getNombre().isEmpty()) {
            usuario.setNombre(dtoRegistro.getNombre());
        }

        // Si se proporciona nueva contraseña
        if (dtoRegistro.getPassword() != null && !dtoRegistro.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        }

        usuariosRepository.save(usuario);

        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Usuario actualizado exitosamente");
        response.put("username", usuario.getUsername());
        response.put("email", usuario.getEmail());
        response.put("nombre", usuario.getNombre());

        return ResponseEntity.ok(response);
    }

    // Método para poder loguear un usuario y obtener un token
    @PostMapping("login")
    public ResponseEntity<DtoAuthRespuesta> login(@RequestBody DtoLogin dtoLogin) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dtoLogin.getUsername(), dtoLogin.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerador.generarToken(authentication);
        Usuarios usuario = usuariosRepository.findByUsername(dtoLogin.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        String role = usuario.getRoles().get(0).getName();
        return new ResponseEntity<>(new DtoAuthRespuesta(token, role), HttpStatus.OK);
    }

    @GetMapping(value = "info-usuario", headers = "Accept=application/json")
    public ResponseEntity<?> obtenerInfoUsuario(Authentication authentication) {

        System.out.println("=== INFO-USUARIO ENDPOINT CALLED ===");
        System.out.println("Authentication: " + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("Usuario NO autenticado");
            return ResponseEntity.status(401).build();
        }
        Usuarios usuario = usuariosRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        System.out.println("Usuario autenticado: " + authentication.getName());

        // Crear respuesta
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("autenticado", true);
        respuesta.put("username", authentication.getName());
        respuesta.put("userId", usuario.getIdUsuario());
        respuesta.put("email", usuario.getEmail());
        respuesta.put("nombre", usuario.getNombre());
        respuesta.put("roles", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        System.out.println("Respuesta: " + respuesta);

        return ResponseEntity.ok(respuesta);
    }

}