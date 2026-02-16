package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Salas;
import com.javadevs.springapirest.repositories.ISalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private ISalaRepository salaRepo;

    @Autowired
    public SalaService(ISalaRepository salaRepo) {
        this.salaRepo = salaRepo;
    }

    //creamos un sala
    public void crear(Salas sala) {
        salaRepo.save(sala);
    }

    //obtenemos toda una lista de sala
    public List<Salas> readAll() {
        return salaRepo.findAll();
    }

    //obtenemos un sala por su id
    public Optional<Salas> readOne(Long id) {
        return salaRepo.findById(id);
    }

    //actualizamos un sala
    public void update(Salas sala) {
        salaRepo.save(sala);
    }

    //eliminamos un sala, implementado al ginal
    public void delete(Long id) {
        salaRepo.deleteById(id);
    }

}
