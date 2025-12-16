package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Asiento;
import com.javadevs.springapirest.repositories.IAsientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsientoService {

    private final IAsientoRepository asientoRepository;

    @Autowired
    public AsientoService(IAsientoRepository asientoRepository) {
        this.asientoRepository = asientoRepository;
    }


        // Validar que la sala esté presente
    public Asiento crear(Asiento asiento) {
        // Validar que la sala esté presente
        if (asiento.getSala() == null || asiento.getSala().getIdsala() == null) {
            throw new RuntimeException("La sala es obligatoria para crear un asiento");
        }

        // CORREGIDO: Usa el método correcto del repositorio
        boolean existe = asientoRepository.existsByNumeroAsientoAndSalaId(
                asiento.getNumeroAsiento(),
                asiento.getSala().getIdsala()  // getIdsala() está bien
        );

        if (existe) {
            throw new RuntimeException("Ya existe un asiento con el número " +
                    asiento.getNumeroAsiento() + " en esta sala");
        }

        // Validar que el número de asiento no sea nulo o vacío
        if (asiento.getNumeroAsiento() == null || asiento.getNumeroAsiento().trim().isEmpty()) {
            throw new RuntimeException("El número de asiento es obligatorio");
        }

        // Asegurar que el estado por defecto sea true (disponible)
        if (asiento.getEstado() == null) {
            asiento.setEstado(true);
        }

        return asientoRepository.save(asiento);
    }

    // Obtener todos los asientos
    public List<Asiento> readAll() {
        return asientoRepository.findAll();
    }

    // Obtener un asiento por su ID
    public Optional<Asiento> readOne(Long id) {
        return asientoRepository.findById(id);
    }

    // Actualizar un asiento
    public Asiento update(Asiento asiento) {
        if (!asientoRepository.existsById(asiento.getIdAsiento())) {
            throw new RuntimeException("Asiento no encontrado con ID: " + asiento.getIdAsiento());
        }
        return asientoRepository.save(asiento);
    }

    // Eliminar un asiento por ID
    public void delete(Long id) {
        if (!asientoRepository.existsById(id)) {
            throw new RuntimeException("Asiento no encontrado con ID: " + id);
        }
        asientoRepository.deleteById(id);
    }

    // Obtener asientos por sala
    public List<Asiento> findBySala(Long idSala) {
        return asientoRepository.findBySala_Idsala(idSala);
    }

    // Obtener asientos disponibles por sala
    public List<Asiento> findDisponiblesBySala(Long idSala) {
        return asientoRepository.findBySala_IdsalaAndEstado(idSala, true);
    }
}