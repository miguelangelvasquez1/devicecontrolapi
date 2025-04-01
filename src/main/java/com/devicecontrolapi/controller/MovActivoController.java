package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.MovActivo;
import com.devicecontrolapi.service.MovActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movactivos")
public class MovActivoController {

    @Autowired
    private MovActivoService movActivoService;

    // Obtener todos los movimientos de activos
    @GetMapping
    public List<MovActivo> obtenerTodosLosMovActivos() {
        return movActivoService.obtenerTodosLosMovActivos();
    }

    // Obtener un movimiento por ID
    @GetMapping("/{id}")
    public Optional<MovActivo> obtenerMovActivoPorId(@PathVariable Integer id) {
        return movActivoService.obtenerMovActivoPorId(id);
    }

    // Buscar movimientos por estado
    @GetMapping("/buscarPorEstado")
    public List<MovActivo> buscarPorEstado(@RequestParam char estado) {
        return movActivoService.buscarPorEstado(estado);
    }

    // Buscar movimientos por activo
    @GetMapping("/buscarPorActivo/{idActivo}")
    public List<MovActivo> buscarPorActivo(@PathVariable Integer idActivo) {
        return movActivoService.buscarPorActivo(idActivo);
    }

    // Buscar movimientos en un rango de fechas
    @GetMapping("/buscarPorRangoFechas")
    public List<MovActivo> buscarPorRangoFechas(@RequestParam Date fechaInicio, @RequestParam Date fechaFin) {
        return movActivoService.buscarPorRangoFechas(fechaInicio, fechaFin);
    }

    // Crear o actualizar un movimiento de activo
    @PostMapping
    public MovActivo guardarMovActivo(@RequestBody MovActivo movActivo) {
        return movActivoService.guardarMovActivo(movActivo);
    }

    // Eliminar un movimiento de activo por ID
    @DeleteMapping("/{id}")
    public void eliminarMovActivo(@PathVariable Integer id) {
        movActivoService.eliminarMovActivo(id);
    }
}
