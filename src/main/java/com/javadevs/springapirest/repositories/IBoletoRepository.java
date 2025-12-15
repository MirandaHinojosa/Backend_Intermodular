package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Boletos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IBoletoRepository extends JpaRepository<Boletos, Long>{


    //boletos por sesión
    @Query("SELECT b FROM Boletos b WHERE b.sesion.idsesion = :sesion_Id")
    List<Boletos> findBySesionId(@Param("sesion_Id") Long sesionId);


    //boletos por estado
    List<Boletos> findByEstado(String estado);


    // Asientos ocupados en una sesión
    @Query("SELECT b.numeroAsiento FROM Boletos b WHERE b.sesion.idsesion = :sesionId AND b.estado IN ('RESERVADO', 'PAGADO')")
    List<String> findAsientosOcupadosBySesionId(@Param("sesionId") Long sesionId);

    // Boletos por fecha específica
    @Query("SELECT b FROM Boletos b WHERE DATE(b.fechaCompra) = :fecha")
    List<Boletos> findByFecha(@Param("fecha") LocalDate fecha);


    //verificar si un asiento esta ocupado
    @Query("SELECT COUNT(b) > 0 FROM Boletos b WHERE b.sesion.idsesion = :sesionId " +
            "AND b.numeroAsiento = :numeroAsiento AND b.estado IN ('RESERVADO', 'PAGADO')")
    boolean isAsientoOcupado(@Param("sesionId") Long sesionId, @Param("numeroAsiento") String numeroAsiento);
}
