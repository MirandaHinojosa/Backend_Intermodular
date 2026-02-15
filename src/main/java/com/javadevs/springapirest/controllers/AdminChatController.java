package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.dtos.ChatMensajeDTO;
import com.javadevs.springapirest.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/chat")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ADMIN responde a un usuario
    @PostMapping("/responder")
    public ResponseEntity<?> responderUsuario(@RequestBody Map<String, String> request) {
        try {
            String receiverEmail = request.get("receiverEmail");
            String contenido = request.get("contenido");

            chatService.enviarRespuestaAdmin(receiverEmail, contenido);

            Map<String, Object> response = new HashMap<>();
            response.put("estado", "success");
            response.put("mensaje", "Respuesta enviada a " + receiverEmail);
            response.put("timestamp", java.time.LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al enviar respuesta: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    // NUEVO ENDPOINT: Obtener usuarios con conversaciones activas
    @GetMapping("/usuarios-activos")
    public ResponseEntity<?> obtenerUsuariosActivos() {
        try {
            // Obtener todos los usuarios con conversaciones activas del ChatService
            List<Map<String, Object>> usuariosActivos = chatService.obtenerUsuariosConChatActivo();

            Map<String, Object> response = new HashMap<>();
            response.put("estado", "success");
            response.put("totalUsuarios", usuariosActivos.size());
            response.put("usuarios", usuariosActivos);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error obteniendo usuarios activos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // NUEVO ENDPOINT: Obtener historial de chat de un usuario específico (para admin)
    @GetMapping("/historial/{emailUsuario}")
    public ResponseEntity<?> obtenerHistorialUsuario(@PathVariable String emailUsuario) {
        try {
            List<Map<String, Object>> historial = chatService.obtenerHistorial(emailUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("estado", "success");
            response.put("email", emailUsuario);
            response.put("totalMensajes", historial.size());
            response.put("historial", historial);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error obteniendo historial: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // NUEVO ENDPOINT: Cerrar chat de un usuario (admin puede forzar cierre)
    @PostMapping("/cerrar/{emailUsuario}")
    public ResponseEntity<?> cerrarChatUsuario(@PathVariable String emailUsuario) {
        try {
            Map<String, Object> resultado = chatService.cerrarChat(emailUsuario);

            Map<String, Object> response = new HashMap<>();
            response.put("estado", "success");
            response.put("mensaje", "Chat cerrado para " + emailUsuario);
            response.put("resultado", resultado);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error cerrando chat: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @MessageMapping("/chat.enviar")
    public void recibirMensajeWebSocket(ChatMensajeDTO mensajeDTO) {
        try {
            // Determinar si es admin o usuario
            boolean esAdmin = "admin@cine.com".equals(mensajeDTO.getSenderEmail());

            if (esAdmin) {
                // Si es admin enviando a un usuario
                if (mensajeDTO.getReceiverEmail() != null) {
                    chatService.enviarRespuestaAdmin(mensajeDTO.getReceiverEmail(), mensajeDTO.getContenido());
                }
            } else {
                // Si es usuario enviando al admin
                // Primero enviar al canal del usuario (para que él lo vea)
                messagingTemplate.convertAndSendToUser(
                        mensajeDTO.getSenderEmail(),
                        "/queue/messages",
                        chatService.prepararMensajeParaWebSocket(mensajeDTO, false)
                );

                // Luego enviar al canal general para que el admin lo vea
                messagingTemplate.convertAndSend(
                        "/topic/admin/chat",
                        chatService.prepararMensajeParaWebSocket(mensajeDTO, false)
                );

                // Guardar en historial - ahora usando el método público
                chatService.guardarMensajeEnHistorial(mensajeDTO.getSenderEmail(), mensajeDTO, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}