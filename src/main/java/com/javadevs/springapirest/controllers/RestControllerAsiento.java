package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.models.Asiento;
import com.javadevs.springapirest.services.AsientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    //Petición para crear un asiento
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public ResponseEntity<Asiento> crearAsiento(@RequestBody Asiento asiento) {
        try {
            Asiento nuevoAsiento = asientoService.crear(asiento);
            return ResponseEntity.ok(nuevoAsiento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // Petición para obtener todos los asientos en la BD
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Asiento> listarAsientos() {
        return asientoService.readAll();
    }

    //Petición para obtener asiento mediante "ID"
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public ResponseEntity<Asiento> obtenerAsientoPorId(@PathVariable Long id) {
        Optional<Asiento> asiento = asientoService.readOne(id);
        return asiento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Petición para obtener asientos por sala
    @GetMapping(value = "porsala/{idSala}", headers = "Accept=application/json")
    public List<Asiento> obtenerAsientosPorSala(@PathVariable Long idSala) {
        return asientoService.findBySala(idSala);
    }

    //Petición para obtener asientos disponibles por sala
    @GetMapping(value = "disponibles/{idSala}", headers = "Accept=application/json")
    public List<Asiento> obtenerAsientosDisponibles(@PathVariable Long idSala) {
        return asientoService.findDisponiblesBySala(idSala);
    }

    //Petición para actualizar un asiento
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public ResponseEntity<Asiento> actualizarAsiento(@RequestBody Asiento asiento) {
        try {
            Asiento asientoActualizado = asientoService.update(asiento);
            return ResponseEntity.ok(asientoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //Petición para eliminar un asiento por "Id"
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public ResponseEntity<Void> eliminarAsiento(@PathVariable Long id) {
        try {
            asientoService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}