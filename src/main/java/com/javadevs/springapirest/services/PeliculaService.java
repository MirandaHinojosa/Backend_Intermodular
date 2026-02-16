package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Peliculas;
import com.javadevs.springapirest.repositories.IPeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PeliculaService {

    private IPeliculaRepository peliculaRepo;

    @Autowired
    public PeliculaService(IPeliculaRepository peliculaRepo) {
        this.peliculaRepo = peliculaRepo;
    }

    //creamos un pelicula
    public void crear(Peliculas pelicula) {
        peliculaRepo.save(pelicula);
    }

    //obtenemos toda una lista de pelicula
    public List<Peliculas> readAll() {
        return peliculaRepo.findAll();
    }

    public List<Peliculas> readAllConSesionesFuturas() {
        LocalDate fechaActual = LocalDate.now();
        return peliculaRepo.findPeliculasConSesionesFuturas(fechaActual);
    }

    //obtenemos un pelicula por su id
    public Optional<Peliculas> readOne(Long id) {
        return peliculaRepo.findById(id);
    }

    //actualizamos un pelicula
    public void update(Peliculas pelicula) {
        peliculaRepo.save(pelicula);
    }

    //eliminamos un pelicula
    public void delete(Long id) {
        peliculaRepo.deleteById(id);
    }

}
