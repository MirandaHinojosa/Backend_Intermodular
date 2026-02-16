package com.javadevs.springapirest.services;

import com.javadevs.springapirest.models.Boletos;
import com.javadevs.springapirest.models.Sesiones;
import com.javadevs.springapirest.models.Usuarios;
import com.javadevs.springapirest.dtos.BoletoRequestDTO;
import com.javadevs.springapirest.repositories.IBoletoRepository;
import com.javadevs.springapirest.repositories.IUsuariosRepository;
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
    private final IUsuariosRepository usuariosRepository;

    @Autowired
    public BoletoService(IBoletoRepository boletoRepository, SesionService sesionService,IUsuariosRepository usuariosRepository) {
        this.boletoRepository = boletoRepository;
        this.sesionService = sesionService;
        this.usuariosRepository = usuariosRepository;
    }
    @Transactional
    public Boletos crearDesdeDTO(BoletoRequestDTO dto) throws Exception {
        Boletos boleto = new Boletos();

        // Asignar usuario si viene en el DTO
        if (dto.getUsuarioId() != null) {
            Usuarios usuario = usuariosRepository.findById(dto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + dto.getUsuarioId()));
            boleto.setUsuario(usuario);
        }
        //Si no viene usuarioId, boleto.getUsuario() será null (boleto sin usuario)

        //Obtener y asignar sesión
        Sesiones sesion = sesionService.readOne(dto.getSesionId())
                .orElseThrow(() -> new Exception("Sesión no encontrada con ID: " + dto.getSesionId()));
        boleto.setSesion(sesion);

        boleto.setNumeroAsiento(dto.getNumeroAsiento());
        boleto.setPrecioPagado(dto.getPrecioPagado());
        boleto.setTipoEntrada(dto.getTipoEntrada());
        boleto.setFechaCompra(LocalDateTime.now());
        boleto.setEstado("RESERVADO"); // Estado inicial

        return crear(boleto);
    }

    //Crear boleto con validaciones
    @Transactional
    public Boletos crear(Boletos boleto) throws Exception {
        // Validaciones básicas
        validarBoleto(boleto);

        //Buscar y asignar usuario si viene con ID
        if (boleto.getUsuario() != null && boleto.getUsuario().getIdUsuario() != null) {
            Long usuarioId = boleto.getUsuario().getIdUsuario();
            Usuarios usuario = usuariosRepository.findById(usuarioId)
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + usuarioId));
            boleto.setUsuario(usuario);
        } else if (boleto.getUsuario() == null && boleto.getUsuarioId() != null) {
            // Si viene un usuarioId separado (como en el DTO)
            Usuarios usuario = usuariosRepository.findById(boleto.getUsuarioId())
                    .orElseThrow(() -> new Exception("Usuario no encontrado con ID: " + boleto.getUsuarioId()));
            boleto.setUsuario(usuario);
        }
        // Si no hay usuario, se deja como null (venta en taquilla)

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

        //Validar asientos disponibles
        if (sesion.getAsientosDisponibles() == null) {
            throw new Exception("El valor de asientos disponibles es nulo");
        }

        if (sesion.getAsientosDisponibles() <= 0) {
            throw new Exception("No hay asientos disponibles para esta sesión");
        }

        //Validar fecha de la sesión
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaSesion = LocalDateTime.of(sesion.getFecha(), sesion.getHora());
        if (fechaSesion.isBefore(ahora)) {
            throw new Exception("La sesión ya ha comenzado o ha pasado");
        }

        //Reducir asientos disponibles
        sesion.setAsientosDisponibles(sesion.getAsientosDisponibles() - 1);

        //Si no hay fecha de compra, usar la fecha actual
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

    // Eliminar boleto, no se implementa
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

    // Cancelar boleto (cambio de estado), no se implementa
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

    // Métodos auxiliares privados, no se implementa
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

    public List<Boletos> findByUsuarioId(Long usuarioId) {
        return boletoRepository.findByUsuarioId(usuarioId);
    }



}
