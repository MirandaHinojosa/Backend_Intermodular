package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Boletos;
import com.javadevs.springapirest.models.Sesiones;
import com.javadevs.springapirest.repositories.IBoletoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoletoService {

    private  IBoletoRepository boletoRepository;
    private  SesionService sesionService;

    @Autowired
    public BoletoService(IBoletoRepository boletoRepository, SesionService sesionService) {
        this.boletoRepository = boletoRepository;
        this.sesionService = sesionService;
    }

    // Crear boleto con validaciones
    @Transactional
    public Boletos crear(Boletos boleto) throws Exception {
        // Validaciones básicas
        validarBoleto(boleto);

        // Verificar que la sesión existe y cargarla completa
        if (boleto.getSesion() == null || boleto.getSesion().getIdsesion() == null) {
            throw new Exception("El ID de sesión es requerido");
        }

        // Cargar la sesión completa desde la base de datos
        Sesiones sesion = sesionService.readOne(boleto.getSesion().getIdsesion())
                .orElseThrow(() -> new Exception("Sesión no encontrada con ID: " + boleto.getSesion().getIdsesion()));

        boleto.setSesion(sesion); // Asignar la sesión completa

        // Verificar disponibilidad de asientos
        if (boletoRepository.isAsientoOcupado(
                sesion.getIdsesion(),
                boleto.getNumeroAsiento())) {
            throw new Exception("El asiento " + boleto.getNumeroAsiento() + " ya está ocupado");
        }

        // Validar asientos disponibles
        if (sesion.getAsientosDisponibles() == null) {
            throw new Exception("El valor de asientos disponibles es nulo");
        }

        if (sesion.getAsientosDisponibles() <= 0) {
            throw new Exception("No hay asientos disponibles para esta sesión");
        }

        // Validar fecha de la sesión
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaSesion = LocalDateTime.of(sesion.getFecha(), sesion.getHora());
        if (fechaSesion.isBefore(ahora)) {
            throw new Exception("La sesión ya ha comenzado o ha pasado");
        }

        // Reducir asientos disponibles
        sesion.setAsientosDisponibles(sesion.getAsientosDisponibles() - 1);

        // Si no hay fecha de compra, usar la fecha actual
        if (boleto.getFechaCompra() == null) {
            boleto.setFechaCompra(LocalDateTime.now());
        }

        return boletoRepository.save(boleto);
    }

    // Obtener todos los boletos
    public List<Boletos> readAll() {
        return boletoRepository.findAll();
    }

    // Obtener boleto por ID
    public Optional<Boletos> readOne(Long id) {
        return boletoRepository.findById(id);
    }

    // Actualizar boleto
    @Transactional
    public Boletos update(Boletos boleto) throws Exception {
        validarBoleto(boleto);
        return boletoRepository.save(boleto);
    }

    // Eliminar boleto
    @Transactional
    public void delete(Long id) throws Exception {
        Optional<Boletos> boletoOpt = boletoRepository.findById(id);
        if (boletoOpt.isPresent()) {
            Boletos boleto = boletoOpt.get();

            // Liberar asiento si el boleto estaba activo
            if (boleto.getEstado().equals("PAGADO") || boleto.getEstado().equals("RESERVADO")) {
                Sesiones sesion = boleto.getSesion();
                sesion.setAsientosDisponibles(sesion.getAsientosDisponibles() + 1);
            }

            boletoRepository.deleteById(id);
        } else {
            throw new Exception("Boleto no encontrado con ID: " + id);
        }
    }

    // Cancelar boleto (cambio de estado)
    @Transactional
    public void cancelarBoleto(Long id) throws Exception {
        Optional<Boletos> boletoOpt = boletoRepository.findById(id);
        if (boletoOpt.isPresent()) {
            Boletos boleto = boletoOpt.get();

            // Solo se pueden cancelar boletos pagados o reservados
            if (boleto.getEstado().equals("PAGADO") || boleto.getEstado().equals("RESERVADO")) {
                boleto.setEstado("CANCELADO");

                // Liberar asiento
                Sesiones sesion = boleto.getSesion();
                sesion.setAsientosDisponibles(sesion.getAsientosDisponibles() + 1);

                boletoRepository.save(boleto);
            } else {
                throw new Exception("No se puede cancelar un boleto en estado: " + boleto.getEstado());
            }
        } else {
            throw new Exception("Boleto no encontrado");
        }
    }

    //metodos de búsqueda específicos
    public List<Boletos> findBySesionId(Long sesionId) {
        return boletoRepository.findBySesionId(sesionId);
    }

    public List<Boletos> findByEstado(String estado) {
        return boletoRepository.findByEstado(estado);
    }


    public List<Boletos> findByFecha(LocalDate fecha) {
        return boletoRepository.findByFecha(fecha);
    }


    public List<String> getAsientosOcupados(Long sesionId) {
        return boletoRepository.findAsientosOcupadosBySesionId(sesionId);
    }

    // Métodos auxiliares privados
    private void validarBoleto(Boletos boleto) throws Exception {
        if (boleto.getSesion() == null) {
            throw new Exception("La sesión es requerida");
        }
        if (boleto.getNumeroAsiento() == null || boleto.getNumeroAsiento().isEmpty()) {
            throw new Exception("El número de asiento es requerido");
        }
        if (boleto.getPrecioPagado() == null || boleto.getPrecioPagado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El precio pagado debe ser mayor a cero");
        }


    }





}
