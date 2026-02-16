package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAsientoRepository extends JpaRepository<Asiento, Long> {


    List<Asiento> findBySala_Idsala(Long idsala);


    List<Asiento> findBySala_IdsalaAndEstado(Long idsala, Boolean estado);


    @Query("SELECT COUNT(a) > 0 FROM Asiento a WHERE a.numeroAsiento = :numeroAsiento AND a.sala.idsala = :idsala")
    boolean existsByNumeroAsientoAndSalaId(@Param("numeroAsiento") String numeroAsiento,
                                           @Param("idsala") Long idsala);

}