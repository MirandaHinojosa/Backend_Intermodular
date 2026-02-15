package com.javadevs.springapirest.models;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="sala")
public class Salas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Long idsala;
    private long numeroSala;
    private long capacidad;
    private long fila;
    private long columna;
    private String tipoSala;
    private Boolean estado;


}