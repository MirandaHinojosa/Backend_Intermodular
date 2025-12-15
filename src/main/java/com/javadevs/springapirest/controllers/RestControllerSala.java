package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.models.Salas;
import com.javadevs.springapirest.services.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/sala/")
public class RestControllerSala {

    private SalaService salaService;

    @Autowired
    public RestControllerSala(SalaService salaService) {
        this.salaService = salaService;
    }

    //Petición para crear una sala
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public void crearSala(@RequestBody Salas sala) {
        salaService.crear(sala);
    }

    //Petición para obtener todos los sala en la BD
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Salas> listarSala() {
        return salaService.readAll();
    }

    //Petición para obtener sala mediante "ID"
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public Optional<Salas> obtenerSalaPorId(@PathVariable Long id) {
        return salaService.readOne(id);
    }

    //Petición para actualizar un sala
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public void actualizarSala(@RequestBody Salas sala) {
        salaService.update(sala);
    }

    //Petición para eliminar un sala por "Id"
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public void eliminarSala(@PathVariable Long id) {
        salaService.delete(id);
    }

}
