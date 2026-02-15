package com.javadevs.springapirest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoDTO {
    private Long idAsiento;
    private String numeroAsiento;
    private String fila;
    private Integer columna;
    private String tipoAsiento;
    private Boolean estado;
    private Long idSala; // Solo el ID de la sala
    private String nombreSala; // O nombre si lo necesitas
}