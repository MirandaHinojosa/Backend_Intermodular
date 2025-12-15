package com.javadevs.springapirest.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(length = 10)
    private String numeroAsiento;


    private LocalDateTime fechaCompra;

    @Column(name = "precio_pagado", precision = 6, scale = 2)
    private BigDecimal precioPagado;

    //reservado, pagado, cancelado
    @Column(length = 20)
    private String estado;


    @Column(name = "tipo_entrada", length = 20)
    private String tipoEntrada; //adulot, menor, estudiante

}
