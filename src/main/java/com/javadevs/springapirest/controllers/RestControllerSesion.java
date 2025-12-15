package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.models.Sesiones;
import com.javadevs.springapirest.services.SesionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sesiones/")
public class RestControllerSesion {

    private SesionService sesionService;

    @Autowired
    public RestControllerSesion(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    // Petición para crear una sesión
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public void crearSesion(@RequestBody Sesiones sesion) {
        sesionService.crear(sesion);
    }

    // Petición para obtener todas las sesiones en la BD
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Sesiones> listarSesiones() {
        return sesionService.readAll();
    }

    // Petición para obtener sesión mediante "ID"
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public Optional<Sesiones> obtenerSesionPorId(@PathVariable Long id) {
        return sesionService.readOne(id);
    }

    // Petición para actualizar una sesión
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public void actualizarSesion(@RequestBody Sesiones sesion) {
        sesionService.update(sesion);
    }

    // Petición para eliminar una sesión por "Id"
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public void eliminarSesion(@PathVariable Long id) {
        sesionService.delete(id);
    }

    // Petición para obtener sesiones por película
    @GetMapping(value = "pelicula/{peliculaId}", headers = "Accept=application/json")
    public List<Sesiones> obtenerSesionesPorPelicula(@PathVariable Long peliculaId) {
        return sesionService.findByPeliculaId(peliculaId);
    }

    // Petición para obtener sesiones por sala
    @GetMapping(value = "sala/{salaId}", headers = "Accept=application/json")
    public List<Sesiones> obtenerSesionesPorSala(@PathVariable Long salaId) {
        return sesionService.findBySalaId(salaId);
    }

    // Petición para obtener sesiones por fecha
    @GetMapping(value = "fecha/{fecha}", headers = "Accept=application/json")
    public List<Sesiones> obtenerSesionesPorFecha(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        return sesionService.findByFecha(fecha);
    }

    // Petición para obtener sesiones con asientos disponibles
    //@GetMapping(value = "disponibles", headers = "Accept=application/json")
    //public List<Sesiones> obtenerSesionesDisponibles() {
    //   return sesionService.findSesionesDisponibles();
    //}

    // Petición para obtener sesiones por fecha y película
    @GetMapping(value = "fecha/{fecha}/pelicula/{peliculaId}", headers = "Accept=application/json")
    public List<Sesiones> obtenerSesionesPorFechaYPelicula(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha,
            @PathVariable Long peliculaId) {
        return sesionService.findByFechaAndPeliculaId(fecha, peliculaId);
    }

}
