package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.service.MovEspacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movespacios")
public class MovEspacioController {

    @Autowired
    private MovEspacioService movEspacioService;

    @PutMapping("/aprobarSol/{id:\\d+}")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Integer id) {
        try {
            movEspacioService.aprobarSolicitud(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // Esto evita que devuelva un HTML feo
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error inesperado en el servidor: " + e.getMessage());
        }
    }

    @PutMapping("/rechazar/{id:\\d+}")
    public ResponseEntity<Void> rechazarSolicitud(@PathVariable("id") Integer id) {
        movEspacioService.rechazarSolicitud(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarSolicitud(@PathVariable("id") Integer id) {
        movEspacioService.cancelarSolicitud(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener todos los movimientos de espacios
    @GetMapping
    public List<MovEspacio> obtenerTodosLosMovEspacios() {
        return movEspacioService.obtenerTodosLosMovEspacios();
    }

    // Obtener un movimiento de espacio por ID
    @GetMapping("/{id}")
    public Optional<MovEspacio> obtenerMovEspacioPorId(@PathVariable Integer id) {
        return movEspacioService.obtenerMovEspacioPorId(id);
    }

    // Buscar movimientos por espacio
    @GetMapping("/buscarPorEspacio/{idEspacio}")
    public List<MovEspacio> buscarPorEspacio(@PathVariable Integer idEspacio) {
        return movEspacioService.buscarPorEspacio(idEspacio);
    }

    // Buscar movimientos activos (sin devolución)
    @GetMapping("/activos")
    public List<MovEspacio> buscarMovimientosActivos() {
        return movEspacioService.buscarMovimientosActivos();
    }

    // Buscar movimientos por usuario
    @GetMapping("/buscarPorUsuario/{idUsuario}")
    public List<MovEspacio> buscarPorUsuario(@PathVariable("idUsuario") Integer idUsuario) {
        return movEspacioService.buscarPorUsuario(idUsuario);
    }

    // Buscar movimientos en un rango de fechas
    @GetMapping("/buscarPorRangoFechas")
    public List<MovEspacio> buscarPorRangoFechas(@RequestParam LocalDateTime fechaInicio,
            @RequestParam LocalDateTime fechaFin) {
        return movEspacioService.buscarPorRangoFechas(fechaInicio, fechaFin);
    }

    // Crear o actualizar un movimiento de espacio
    @PostMapping
    public MovEspacio guardarMovEspacio(@RequestBody MovEspacio movEspacio) {
        return movEspacioService.guardarMovEspacio(movEspacio);
    }

    // Eliminar un movimiento de espacio por ID
    @DeleteMapping("/{id}")
    public void eliminarMovEspacio(@PathVariable Integer id) {
        movEspacioService.eliminarMovEspacio(id);
    }
}
