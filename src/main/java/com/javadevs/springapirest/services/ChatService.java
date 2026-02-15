package com.javadevs.springapirest.services;

import com.javadevs.springapirest.dtos.ChatMensajeDTO;
import com.javadevs.springapirest.models.Usuarios;
import com.javadevs.springapirest.repositories.IUsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private IUsuariosRepository usuariosRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Mapa para rastrear conversaciones activas
    private Map<String, String> conversacionesActivas = new ConcurrentHashMap<>();
    // Mapa para contar mensajes por usuario
    private Map<String, Integer> contadorMensajes = new ConcurrentHashMap<>();
    // Mapa para almacenar historial de mensajes
    private Map<String, List<Map<String, Object>>> historialMensajes = new ConcurrentHashMap<>();

    private static final String ADMIN_EMAIL = "admin@nice.com";

    // Método para INICIAR un nuevo chat
    public Map<String, Object> iniciarChat(ChatMensajeDTO mensajeDTO) {
        Map<String, Object> respuesta = validarYPrepararUsuario(mensajeDTO);
        String senderEmail = mensajeDTO.getSenderEmail();

        // Marcar que este usuario tiene una conversación activa
        conversacionesActivas.put(senderEmail, "ACTIVO");
        contadorMensajes.put(senderEmail, 1); // Primer mensaje

        // Inicializar historial si no existe
        if (!historialMensajes.containsKey(senderEmail)) {
            historialMensajes.put(senderEmail, new ArrayList<>());
        }

        // Enviar mensaje inicial del usuario
        enviarMensajeUsuario(mensajeDTO);

        // Guardar mensaje en historial
        guardarMensajeEnHistorial(senderEmail, mensajeDTO, false);

        // Enviar respuesta automática del ADMIN (ASÍNCRONO para no bloquear)
        enviarRespuestaAutomaticaAsync(senderEmail);

        // Preparar respuesta
        respuesta.put("estado", "success");
        respuesta.put("tipo", "CHAT_INICIADO");
        respuesta.put("mensaje", "Chat iniciado exitosamente");
        respuesta.put("conversacionId", System.currentTimeMillis()); // ID temporal
        respuesta.put("canalWebSocket", "/topic/chat/" + senderEmail);

        return respuesta;
    }

    // Método para ENVIAR mensaje en conversación existente
    public Map<String, Object> enviarMensaje(ChatMensajeDTO mensajeDTO) {
        Map<String, Object> respuesta = validarYPrepararUsuario(mensajeDTO);
        String senderEmail = mensajeDTO.getSenderEmail();

        // Verificar si el usuario tiene una conversación activa
        if (!conversacionesActivas.containsKey(senderEmail)) {
            // Si no tiene conversación activa, iniciar una nueva
            return iniciarChat(mensajeDTO);
        }

        // Incrementar contador de mensajes
        int mensajeNum = contadorMensajes.getOrDefault(senderEmail, 0) + 1;
        contadorMensajes.put(senderEmail, mensajeNum);

        // Enviar mensaje del usuario
        enviarMensajeUsuario(mensajeDTO);

        // Guardar mensaje en historial
        guardarMensajeEnHistorial(senderEmail, mensajeDTO, false);

        // Preparar respuesta
        respuesta.put("estado", "success");
        respuesta.put("tipo", "MENSAJE_ENVIADO");
        respuesta.put("mensaje", "Mensaje enviado exitosamente");
        respuesta.put("numeroMensaje", mensajeNum);
        respuesta.put("canalWebSocket", "/topic/chat/" + senderEmail);

        return respuesta;
    }

    // Método común para validar usuario
    private Map<String, Object> validarYPrepararUsuario(ChatMensajeDTO mensajeDTO) {
        Map<String, Object> resultado = new HashMap<>();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Usuarios usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el usuario tenga rol USER
        boolean esUser = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("USER"));

        if (!esUser) {
            throw new RuntimeException("Solo usuarios con rol USER pueden usar el chat");
        }

        // Verificar que el correo coincida o asignarlo
        if (mensajeDTO.getSenderEmail() == null || !usuario.getEmail().equals(mensajeDTO.getSenderEmail())) {
            mensajeDTO.setSenderEmail(usuario.getEmail());
        }

        resultado.put("usuario", usuario);
        return resultado;
    }

    private void enviarMensajeUsuario(ChatMensajeDTO mensajeDTO) {
        Map<String, Object> mensajeWebSocket = new HashMap<>();
        mensajeWebSocket.put("senderEmail", mensajeDTO.getSenderEmail());
        mensajeWebSocket.put("receiverEmail", ADMIN_EMAIL); // USA LA CONSTANTE
        mensajeWebSocket.put("contenido", mensajeDTO.getContenido());
        mensajeWebSocket.put("fechaHora", LocalDateTime.now().toString());
        mensajeWebSocket.put("esAutomatico", false);
        mensajeWebSocket.put("tipo", "MENSAJE_USUARIO");
        mensajeWebSocket.put("id", System.currentTimeMillis());

        messagingTemplate.convertAndSend(
                "/topic/chat/" + mensajeDTO.getSenderEmail(),
                mensajeWebSocket
        );

        // ENVIAR TAMBIÉN AL TOPIC DEL ADMIN
        messagingTemplate.convertAndSend(
                "/topic/admin/chat",
                mensajeWebSocket
        );
    }

    @Async
    public void enviarRespuestaAutomaticaAsync(String senderEmail) {
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Map<String, Object> respuestaAutomatica = new HashMap<>();
                    respuestaAutomatica.put("senderEmail", ADMIN_EMAIL); // USA LA CONSTANTE
                    respuestaAutomatica.put("receiverEmail", senderEmail);
                    respuestaAutomatica.put("contenido", "¡Hola! Soy el Administrador. En un momento me pondré en contacto contigo. Gracias por tu paciencia.");
                    respuestaAutomatica.put("fechaHora", LocalDateTime.now().toString());
                    respuestaAutomatica.put("esAutomatico", true);
                    respuestaAutomatica.put("tipo", "RESPUESTA_AUTOMATICA");
                    respuestaAutomatica.put("id", System.currentTimeMillis());

                    messagingTemplate.convertAndSend(
                            "/topic/chat/" + senderEmail,
                            respuestaAutomatica
                    );

                    // Guardar en historial
                    ChatMensajeDTO mensajeAuto = new ChatMensajeDTO();
                    mensajeAuto.setSenderEmail(ADMIN_EMAIL);
                    mensajeAuto.setReceiverEmail(senderEmail);
                    mensajeAuto.setContenido(respuestaAutomatica.get("contenido").toString());
                    mensajeAuto.setEsAutomatico(true);
                    guardarMensajeEnHistorial(senderEmail, mensajeAuto, true);

                    timer.cancel();
                }
            }, 1500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para que ADMIN responda
    public void enviarRespuestaAdmin(String receiverEmail, String contenido) {
        Map<String, Object> respuestaAdmin = new HashMap<>();
        respuestaAdmin.put("senderEmail", ADMIN_EMAIL); // USA LA CONSTANTE
        respuestaAdmin.put("receiverEmail", receiverEmail);
        respuestaAdmin.put("contenido", contenido);
        respuestaAdmin.put("fechaHora", LocalDateTime.now().toString());
        respuestaAdmin.put("esAutomatico", false);
        respuestaAdmin.put("tipo", "RESPUESTA_ADMIN");
        respuestaAdmin.put("id", System.currentTimeMillis());

        // Enviar al usuario
        messagingTemplate.convertAndSend(
                "/topic/chat/" + receiverEmail,
                respuestaAdmin
        );

        // Enviar también al topic del admin para que todos los admins vean la respuesta
        messagingTemplate.convertAndSend(
                "/topic/admin/chat",
                respuestaAdmin
        );

        // Guardar en historial
        ChatMensajeDTO mensajeAdmin = new ChatMensajeDTO();
        mensajeAdmin.setSenderEmail(ADMIN_EMAIL);
        mensajeAdmin.setReceiverEmail(receiverEmail);
        mensajeAdmin.setContenido(contenido);
        mensajeAdmin.setEsAutomatico(false);
        guardarMensajeEnHistorial(receiverEmail, mensajeAdmin, false);
    }

    // Método para cerrar conversación
    public Map<String, Object> cerrarChat(String emailUsuario) {
        conversacionesActivas.remove(emailUsuario);
        contadorMensajes.remove(emailUsuario);
        historialMensajes.remove(emailUsuario);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("estado", "success");
        respuesta.put("mensaje", "Chat cerrado exitosamente");
        respuesta.put("email", emailUsuario);

        return respuesta;
    }

    // Método para ver estado del chat (MEJORADO)
    public Map<String, Object> estadoChat(String emailUsuario) {
        Map<String, Object> estado = new HashMap<>();
        estado.put("email", emailUsuario);
        estado.put("activo", conversacionesActivas.containsKey(emailUsuario));
        estado.put("totalMensajes", contadorMensajes.getOrDefault(emailUsuario, 0));
        estado.put("fechaHora", LocalDateTime.now().toString());
        estado.put("conversacionId", System.currentTimeMillis());
        estado.put("tieneChatActivo", conversacionesActivas.containsKey(emailUsuario));

        // Incluir últimos mensajes del historial
        if (historialMensajes.containsKey(emailUsuario)) {
            List<Map<String, Object>> mensajes = historialMensajes.get(emailUsuario);
            if (!mensajes.isEmpty()) {
                estado.put("ultimoMensaje", mensajes.get(mensajes.size() - 1).get("contenido"));
                estado.put("historial", mensajes.subList(Math.max(0, mensajes.size() - 5), mensajes.size()));
            }
        }

        return estado;
    }

    // Método para obtener historial completo
    public List<Map<String, Object>> obtenerHistorial(String emailUsuario) {
        return historialMensajes.getOrDefault(emailUsuario, new ArrayList<>());
    }

    // CAMBIADO: De privado a público para que pueda ser usado por AdminChatController
    public void guardarMensajeEnHistorial(String emailUsuario, ChatMensajeDTO mensaje, boolean esAutomatico) {
        if (!historialMensajes.containsKey(emailUsuario)) {
            historialMensajes.put(emailUsuario, new ArrayList<>());
        }

        Map<String, Object> mensajeHistorial = new HashMap<>();
        mensajeHistorial.put("senderEmail", mensaje.getSenderEmail());
        mensajeHistorial.put("receiverEmail", mensaje.getReceiverEmail());
        mensajeHistorial.put("contenido", mensaje.getContenido());
        mensajeHistorial.put("fechaHora", LocalDateTime.now().toString());
        mensajeHistorial.put("esAutomatico", esAutomatico);
        mensajeHistorial.put("tipo", esAutomatico ? "RESPUESTA_AUTOMATICA" : "MENSAJE_USUARIO");
        mensajeHistorial.put("id", System.currentTimeMillis());

        historialMensajes.get(emailUsuario).add(mensajeHistorial);
    }

    public List<Map<String, Object>> obtenerUsuariosConChatActivo() {
        List<Map<String, Object>> usuariosActivos = new ArrayList<>();

        // Recorrer conversaciones activas
        for (String email : conversacionesActivas.keySet()) {
            Map<String, Object> usuarioInfo = new HashMap<>();
            usuarioInfo.put("email", email);
            usuarioInfo.put("estado", conversacionesActivas.get(email));
            usuarioInfo.put("totalMensajes", contadorMensajes.getOrDefault(email, 0));
            usuarioInfo.put("ultimaActividad", LocalDateTime.now().toString()); // Podrías guardar timestamp real

            // Obtener usuario de la base de datos si existe
            try {
                Optional<Usuarios> usuarioOpt = usuariosRepository.findByEmail(email);
                if (usuarioOpt.isPresent()) {
                    Usuarios usuario = usuarioOpt.get();
                    usuarioInfo.put("nombre", usuario.getNombre());
                    usuarioInfo.put("username", usuario.getUsername());
                    usuarioInfo.put("roles", usuario.getRoles().stream()
                            .map(rol -> rol.getName())
                            .collect(Collectors.toList()));
                }
            } catch (Exception e) {
                // Si no encuentra el usuario, continuar
            }

            // Obtener últimos mensajes del historial
            if (historialMensajes.containsKey(email)) {
                List<Map<String, Object>> mensajes = historialMensajes.get(email);
                if (!mensajes.isEmpty()) {
                    Map<String, Object> ultimoMensaje = mensajes.get(mensajes.size() - 1);
                    usuarioInfo.put("ultimoMensaje", ultimoMensaje.get("contenido"));
                    usuarioInfo.put("ultimoMensajeFecha", ultimoMensaje.get("fechaHora"));
                }
            }

            usuariosActivos.add(usuarioInfo);
        }

        return usuariosActivos;
    }

    public Map<String, Object> prepararMensajeParaWebSocket(ChatMensajeDTO mensajeDTO, boolean esAutomatico) {
        Map<String, Object> mensajeWebSocket = new HashMap<>();
        mensajeWebSocket.put("senderEmail", mensajeDTO.getSenderEmail());
        mensajeWebSocket.put("receiverEmail", mensajeDTO.getReceiverEmail());
        mensajeWebSocket.put("contenido", mensajeDTO.getContenido());
        mensajeWebSocket.put("fechaHora", LocalDateTime.now().toString());
        mensajeWebSocket.put("esAutomatico", esAutomatico);
        mensajeWebSocket.put("tipo", esAutomatico ? "RESPUESTA_AUTOMATICA" : "MENSAJE_USUARIO");
        mensajeWebSocket.put("id", System.currentTimeMillis());

        return mensajeWebSocket;
    }

    public String obtenerEmailPorUsername(String username) {
        Usuarios usuario = usuariosRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return usuario.getEmail();
    }
}