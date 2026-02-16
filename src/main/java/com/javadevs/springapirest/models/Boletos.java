package com.javadevs.springapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boletos")
public class Boletos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleto")
    private Long idboleto;

    @ManyToOne
    @JoinColumn(name = "sesion_id")
    @JsonIgnoreProperties({"boletos", "pelicula", "sala"})
    private Sesiones sesion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = true) // Puede ser null, al principio lo pensé para boletos que no sea comprados por usuaris
    @JsonIgnoreProperties({"boletos", "roles", "password"})
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Solo para escritura desde JSON
    private Usuarios usuario;

    // Método para recibir el usuarioId desde el JSON
    // Este setter se llamará cuando el JSON tenga "usuarioId": 4
    @JsonProperty("usuarioId")
    public void setUsuarioIdFromJson(Long usuarioId) {
        if (usuarioId != null) {
            this.usuario = new Usuarios();
            this.usuario.setIdUsuario(usuarioId);
        }
    }

    @Column(length = 10)
    private String numeroAsiento;

    private LocalDateTime fechaCompra;

    @Column(name = "precio_pagado", precision = 6, scale = 2)
    private BigDecimal precioPagado;

    //reservado, pagado, cancelado
    @Column(length = 20)
    private String estado;

    @Column(name = "tipo_entrada", length = 20)
    private String tipoEntrada; //adulto, menor, estudiante

    @Column(name = "Nom_usuario", length = 20)
    private String Nom_usuario; //registramos el usuario en caso de no estar logueado

    // Método para obtener el ID del usuario (útil para respuestas JSON)
    @JsonProperty("usuarioId")
    public Long getUsuarioId() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }
}