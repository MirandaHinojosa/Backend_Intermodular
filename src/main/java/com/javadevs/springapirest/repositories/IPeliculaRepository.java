package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Peliculas;
import com.javadevs.springapirest.models.Sesiones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface IPeliculaRepository extends JpaRepository<Peliculas, Long> {

    @Query("SELECT DISTINCT p FROM Peliculas p JOIN p.sesiones s " +
            "WHERE s.fecha >= :fechaActual AND p.estado = true")
    List<Peliculas> findPeliculasConSesionesFuturas(@Param("fechaActual") LocalDate fechaActual);
}
