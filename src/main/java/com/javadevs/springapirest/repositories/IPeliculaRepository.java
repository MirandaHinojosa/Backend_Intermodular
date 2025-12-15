
package com.javadevs.springapirest.repositories;

import com.javadevs.springapirest.models.Peliculas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPeliculaRepository extends JpaRepository<Peliculas, Long> {
}




