package com.javadevs.springapirest.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletoRequestDTO {
    private Long sesionId;
    private Long usuarioId;
    private String numeroAsiento;
    private BigDecimal precioPagado;
    private String tipoEntrada;

}