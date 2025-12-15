package com.javadevs.springapirest.repositories;


import com.javadevs.springapirest.models.Salas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISalaRepository extends JpaRepository<Salas, Long> {
}
