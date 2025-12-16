package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.models.Asiento;
import com.javadevs.springapirest.services.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asiento/")
public class RestControllerAsiento {

    private AsientoService asientoService;

    @Autowired
    public RestControllerAsiento(AsientoService asientoService) {
        this.asientoService = asientoService;
    }

    // Petición para crear un asiento
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public void crearAsiento(@RequestBody Asiento asiento) {
        asientoService.crear(asiento);
    }

    // Petición para obtener todos los asientos en la BD
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Asiento> listarAsientos() {
        return asientoService.readAll();
    }

    // Petición para obtener asiento mediante "ID"
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public Optional<Asiento> obtenerAsientoPorId(@PathVariable Long id) {
        return asientoService.readOne(id);
    }

    // Petición para obtener asientos por sala
    @GetMapping(value = "por-sala/{idSala}", headers = "Accept=application/json")
    public List<Asiento> obtenerAsientosPorSala(@PathVariable Long idSala) {
        return asientoService.findBySala(idSala);
    }

    // Petición para obtener asientos disponibles por sala
    @GetMapping(value = "disponibles/{idSala}", headers = "Accept=application/json")
    public List<Asiento> obtenerAsientosDisponibles(@PathVariable Long idSala) {
        return asientoService.findDisponiblesBySala(idSala);
    }

    // Petición para actualizar un asiento
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public void actualizarAsiento(@RequestBody Asiento asiento) {
        asientoService.update(asiento);
    }

    // Petición para eliminar un asiento por "Id"
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public void eliminarAsiento(@PathVariable Long id) {
        asientoService.delete(id);
    }
}