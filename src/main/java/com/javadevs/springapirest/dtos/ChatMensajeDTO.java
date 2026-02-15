package com.javadevs.springapirest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMensajeDTO {
    private String senderEmail;
    private String receiverEmail;
    private String contenido;
    private boolean esAutomatico;
}