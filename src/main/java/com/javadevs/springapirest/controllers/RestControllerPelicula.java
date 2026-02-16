package com.javadevs.springapirest.controllers;


import com.javadevs.springapirest.models.Peliculas;
import com.javadevs.springapirest.services.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pelicula/")
public class RestControllerPelicula {

    private PeliculaService peliService;

    @Autowired
    public RestControllerPelicula(PeliculaService peliService) {
        this.peliService = peliService;
    }

    //Petición para crear un  pelicula
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public void crearPelicula(@RequestBody Peliculas pelicula) {
        peliService.crear(pelicula);
    }

    //Petición para obtener todos los pelicula en la BD
    //@GetMapping(value = "listar", headers = "Accept=application/json")
    //public List<Peliculas> listarPeliculas() {
        //return peliService.readAll();
    //}


    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Peliculas> listarPeliculas() {
        return peliService.readAllConSesionesFuturas();
    }

    //especifico para listar todas las pelicuals
    @GetMapping(value = "listar-todas", headers = "Accept=application/json")
    public List<Peliculas> listarTodasPeliculas() {
        return peliService.readAll();
    }

    //Petición para obtener pelicual mediante "ID"
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public Optional<Peliculas> obtenerPeliculaPorId(@PathVariable Long id) {
        return peliService.readOne(id);
    }

    //Petición para actualizar un pelicula
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public void actualizarPelicula(@RequestBody Peliculas pelicula) {
        peliService.update(pelicula);
    }

    //Petición para eliminar un pelicula por "Id"
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public void eliminarPelicula(@PathVariable Long id) {
        peliService.delete(id);
    }

}
