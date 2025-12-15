package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Peliculas;
import com.javadevs.springapirest.repositories.IPeliculaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeliculaService {

    private IPeliculaRepository peliculaRepo;

    @Autowired
    public PeliculaService(IPeliculaRepository peliculaRepo) {
        this.peliculaRepo = peliculaRepo;
    }

    //Creamos un pelicula
    public void crear(Peliculas pelicula) {
        peliculaRepo.save(pelicula);
    }

    //Obtenemos toda una lista de pelicula
    public List<Peliculas> readAll() {
        return peliculaRepo.findAll();
    }

    //Obtenemos un pelicula por su id
    public Optional<Peliculas> readOne(Long id) {
        return peliculaRepo.findById(id);
    }

    //Actualizamos un pelicula
    public void update(Peliculas pelicula) {
        peliculaRepo.save(pelicula);
    }

    //Eliminamos un pelicula
    public void delete(Long id) {
        peliculaRepo.deleteById(id);
    }

}
