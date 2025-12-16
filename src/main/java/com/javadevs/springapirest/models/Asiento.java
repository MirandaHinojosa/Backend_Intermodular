package com.javadevs.springapirest.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asiento")
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asiento")
    private Long idAsiento;

    @Column(name = "numero_asiento", nullable = false)
    private String numeroAsiento;

    @Column(name = "fila", nullable = false)
    private String fila;

    @Column(name = "columna", nullable = false)
    private Integer columna;

    @Column(name = "tipo_asiento") // normal, VIP, preferencial
    private String tipoAsiento;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true; // true = disponible, false = ocupado/no disponible

    // Relación Many-to-One con Sala
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "id_sala",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_asiento_sala")
    )
    private Salas sala;
}