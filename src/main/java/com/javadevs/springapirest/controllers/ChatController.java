package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.dtos.ChatMensajeDTO;
import com.javadevs.springapirest.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Para INICIAR un NUEVO chat (solo primera vez)
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarChat(@RequestBody ChatMensajeDTO mensajeDTO) {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Establecer senderEmail del usuario autenticado
            mensajeDTO.setSenderEmail(username);

            Map<String, Object> respuesta = chatService.iniciarChat(mensajeDTO);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Para ENVIAR mensajes en conversación EXISTENTE
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarMensaje(@RequestBody ChatMensajeDTO mensajeDTO) {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Verificar que el usuario esté enviando desde su propio email
            if (!username.equals(mensajeDTO.getSenderEmail())) {
                mensajeDTO.setSenderEmail(username);
            }

            Map<String, Object> respuesta = chatService.enviarMensaje(mensajeDTO);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Para ver estado de tu chat (AHORA CON USUARIO AUTENTICADO)
    @GetMapping("/estado")
    public ResponseEntity<?> estadoChat() {
        try {
            // Obtener usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();


            String email = chatService.obtenerEmailPorUsername(username);

            Map<String, Object> respuesta = chatService.estadoChat(email);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Para cerrar tu chat (AHORA CON USUARIO AUTENTICADO)
    @PostMapping("/cerrar")
    public ResponseEntity<?> cerrarChat() {
        try {
            // Obtener email del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            Map<String, Object> respuesta = chatService.cerrarChat(username);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // CONVERTIR username a email
            String email = chatService.obtenerEmailPorUsername(username);

            List<Map<String, Object>> historial = chatService.obtenerHistorial(email);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("estado", "success");
            respuesta.put("email", email); // Ahora muestra email, no username
            respuesta.put("totalMensajes", historial.size());
            respuesta.put("historial", historial);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

}