package com.javadevs.springapirest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sesiones")
public class Sesiones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long idsesion;

    @OneToMany(mappedBy = "sesion")
    @JsonIgnore
    private List<Boletos> boletos;

    @ManyToOne
    @JoinColumn(name = "pelicula_id")
    @JsonIgnoreProperties({"sesiones"})
    private Peliculas pelicula;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    @JsonIgnoreProperties({"sesiones"})
    private Salas sala;

    private Integer asientosDisponibles;


    private LocalTime hora;

    @Column(name = "precio", precision = 6, scale = 2)
    private BigDecimal precio;

    private LocalDate fecha;


}