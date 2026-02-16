package com.javadevs.springapirest.controllers;

import com.javadevs.springapirest.models.Boletos;
import com.javadevs.springapirest.models.Usuarios;
import com.javadevs.springapirest.repositories.IUsuariosRepository;
import com.javadevs.springapirest.services.BoletoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boletos")
public class RestControllerBoleto {

    private final BoletoService boletoService;
    private final IUsuariosRepository usuariosRepository;
    private static final Logger logger = LoggerFactory.getLogger(RestControllerBoleto.class);

    @Autowired
    public RestControllerBoleto(BoletoService boletoService, IUsuariosRepository usuariosRepository) {
        this.boletoService = boletoService;
        this.usuariosRepository = usuariosRepository;
    }

    // Crear boleto
    @PostMapping(value = "crear", headers = "Accept=application/json")
    public ResponseEntity<?> crearBoleto(@RequestBody Boletos boleto) {
        try {
            Boletos nuevoBoleto = boletoService.crear(boleto);
            return new ResponseEntity<>(nuevoBoleto, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping(value = "mis-boletos", headers = "Accept=application/json")
    public ResponseEntity<?> obtenerMisBoletos() {
        logger.info("=== PETICIÓN MIS BOLETOS RECIBIDA ===");

        try {
            // Obtener usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            logger.info("Usuario autenticado: {}", username);

            //Buscar usuario por username
            Optional<Usuarios> usuarioOpt = usuariosRepository.findByUsername(username);
            if (!usuarioOpt.isPresent()) {
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            Usuarios usuario = usuarioOpt.get();
            List<Boletos> boletos = boletoService.findByUsuarioId(usuario.getIdUsuario());

            logger.info("Devolviendo {} boletos para usuario {}", boletos.size(), username);
            return new ResponseEntity<>(boletos, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("ERROR al obtener mis boletos: ", e);
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Obtener boleto por ID
    @GetMapping(value = "listarId/{id}", headers = "Accept=application/json")
    public ResponseEntity<?> obtenerBoletoPorId(@PathVariable Long id) {
        Optional<Boletos> boleto = boletoService.readOne(id);
        if (boleto.isPresent()) {
            return new ResponseEntity<>(boleto.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Boleto no encontrado", HttpStatus.NOT_FOUND);
        }
    }

    // Actualizar boleto
    @PutMapping(value = "actualizar", headers = "Accept=application/json")
    public ResponseEntity<?> actualizarBoleto(@RequestBody Boletos boleto) {
        try {
            Boletos boletoActualizado = boletoService.update(boleto);
            return new ResponseEntity<>(boletoActualizado, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Eliminar boleto, no impñementado
    @DeleteMapping(value = "eliminar/{id}", headers = "Accept=application/json")
    public ResponseEntity<?> eliminarBoleto(@PathVariable Long id) {
        try {
            boletoService.delete(id);
            return new ResponseEntity<>("Boleto eliminado correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    //cancelar boleto, no implementado
    @PutMapping(value = "cancelar/{id}", headers = "Accept=application/json")
    public ResponseEntity<?> cancelarBoleto(@PathVariable Long id) {
        try {
            boletoService.cancelarBoleto(id);
            return new ResponseEntity<>("Boleto cancelado correctamente", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    //obtener boletos por sesión
    @GetMapping(value = "sesion/{sesionId}", headers = "Accept=application/json")
    public List<Boletos> obtenerBoletosPorSesion(@PathVariable Long sesionId) {
        return boletoService.findBySesionId(sesionId);
    }

    // Obtener boletos por estado
    @GetMapping(value = "estado/{estado}", headers = "Accept=application/json")
    public List<Boletos> obtenerBoletosPorEstado(@PathVariable String estado) {
        return boletoService.findByEstado(estado);
    }

    // Obtener boletos por fecha
    @GetMapping(value = "fecha/{fecha}", headers = "Accept=application/json")
    public List<Boletos> obtenerBoletosPorFecha(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha) {
        return boletoService.findByFecha(fecha);
    }



    // Obtener asientos ocupados por sesión
    @GetMapping(value = "asientos-ocupados/{sesionId}", headers = "Accept=application/json")
    public List<String> obtenerAsientosOcupados(@PathVariable Long sesionId) {
        return boletoService.getAsientosOcupados(sesionId);
    }



}
