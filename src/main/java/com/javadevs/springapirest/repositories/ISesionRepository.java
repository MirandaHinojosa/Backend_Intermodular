package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Sesiones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ISesionRepository extends JpaRepository<Sesiones, Long> {

    //nombre correcto de la propiedad en Peliculas (idpelicula)
    @Query("SELECT s FROM Sesiones s WHERE s.pelicula.idpelicula = :peliculaId")
    List<Sesiones> findByPeliculaId(@Param("peliculaId") Long peliculaId);

    // nombre correcto de la propiedad en Salas (idsala)
    @Query("SELECT s FROM Sesiones s WHERE s.sala.idsala = :salaId")
    List<Sesiones> findBySalaId(@Param("salaId") Long salaId);

    // propiedades directas de Sesiones
    List<Sesiones> findByFecha(LocalDate fecha);

    //nombre correcto de la propiedad en Peliculas
    @Query("SELECT s FROM Sesiones s WHERE s.fecha = :fecha AND s.pelicula.idpelicula = :peliculaId")
    List<Sesiones> findByFechaAndPeliculaId(@Param("fecha") LocalDate fecha,
                                            @Param("peliculaId") Long peliculaId);


    //se debe modificar esta consulta ya que no estoy segurosi así, debe ser
    //List<Sesiones> findByAsientosDisponiblesGreaterThan(int cantidad);
}
