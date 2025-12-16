package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAsientoRepository extends JpaRepository<Asiento, Long> {

    // CORREGIDO: findBySala_IdSala -> findBySala_Idsala
    List<Asiento> findBySala_Idsala(Long idsala);

    // CORREGIDO: findBySala_IdSalaAndEstado -> findBySala_IdsalaAndEstado
    List<Asiento> findBySala_IdsalaAndEstado(Long idsala, Boolean estado);

    // El método está bien, pero vamos a hacerlo consistente:
    @Query("SELECT COUNT(a) > 0 FROM Asiento a WHERE a.numeroAsiento = :numeroAsiento AND a.sala.idsala = :idsala")
    boolean existsByNumeroAsientoAndSalaId(@Param("numeroAsiento") String numeroAsiento,
                                           @Param("idsala") Long idsala);

}