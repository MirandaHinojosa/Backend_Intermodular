package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Sesiones;
import com.javadevs.springapirest.repositories.ISesionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SesionService {


    private ISesionRepository sesionesRepo;

    @Autowired
    public SesionService(ISesionRepository sesionesRepo) {
        this.sesionesRepo = sesionesRepo;
    }

    // Creamos una sesión
    public void crear(Sesiones sesion) {
        // Si no se especifican asientos disponibles, usar la capacidad de la sala
        if (sesion.getAsientosDisponibles() == null && sesion.getSala() != null) {
            sesion.setAsientosDisponibles(Math.toIntExact(sesion.getSala().getCapacidad()));
        }
        sesionesRepo.save(sesion);
    }

    // Obtenemos toda una lista de sesiones
    public List<Sesiones> readAll() {
        return sesionesRepo.findAll();
    }

    // Obtenemos una sesión por su id
    public Optional<Sesiones> readOne(Long id) {
        return sesionesRepo.findById(id);
    }

    // Actualizamos una sesión
    public void update(Sesiones sesion) {
        sesionesRepo.save(sesion);
    }

    // Eliminamos una sesión
    public void delete(Long id) {
        sesionesRepo.deleteById(id);
    }

    // Obtenemos sesiones por película
    public List<Sesiones> findByPeliculaId(Long peliculaId) {
        return sesionesRepo.findByPeliculaId(peliculaId);
    }

    // Obtenemos sesiones por sala
    public List<Sesiones> findBySalaId(Long salaId) {
        return sesionesRepo.findBySalaId(salaId);
    }

    // Obtenemos sesiones por fecha
    public List<Sesiones> findByFecha(LocalDate fecha) {
        return sesionesRepo.findByFecha(fecha);
    }

    // Obtenemos sesiones por fecha y película
    public List<Sesiones> findByFechaAndPeliculaId(LocalDate fecha, Long peliculaId) {
        return sesionesRepo.findByFechaAndPeliculaId(fecha, peliculaId);
    }

    // Obtenemos sesiones con asientos disponibles
    //public List<Sesiones> findSesionesDisponibles() {
      //  return sesionesRepo.findByAsientosDisponiblesGreaterThan(0);
    //}

}
