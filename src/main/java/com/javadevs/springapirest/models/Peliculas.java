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
@Table(name = "pelicula")
public class Peliculas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Long idpelicula;
    private String titulo;
    private long duracion_minutos;
    private String clasificacion;
    private String genero;
    private Boolean estado;
    private Date fecha_publicacion;

}
