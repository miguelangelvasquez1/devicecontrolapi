package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.TipoActivo;
import com.devicecontrolapi.service.TipoActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tipoactivos")
public class TipoActivoController {

    @Autowired
    private TipoActivoService tipoActivoService;

    // Obtener todos los tipos de activos
    @GetMapping
    public List<TipoActivo> obtenerTodosLosTiposDeActivos() {
        return tipoActivoService.obtenerTodosLosTiposDeActivos();
    }

    // Obtener un tipo de activo por ID
    @GetMapping("/{id}")
    public Optional<TipoActivo> obtenerTipoActivoPorId(@PathVariable Integer id) {
        return tipoActivoService.obtenerTipoActivoPorId(id);
    }

    // Buscar tipos de activos por nombre
    @GetMapping("/buscarPorNombre")
    public List<TipoActivo> buscarPorNombre(@RequestParam String nombre) {
        return tipoActivoService.buscarPorNombre(nombre);
    }

    // Contar el total de tipos de activos
    @GetMapping("/contar")
    public long contarTiposDeActivos() {
        return tipoActivoService.contarTiposDeActivos();
    }

    // Verificar si un tipo de activo ya existe por su nombre
    @GetMapping("/existe")
    public boolean existeTipoActivoConNombre(@RequestParam String nombre) {
        return tipoActivoService.existeTipoActivoConNombre(nombre);
    }

    // Crear o actualizar un tipo de activo
    @PostMapping
    public TipoActivo guardarTipoActivo(@RequestBody TipoActivo tipoActivo) {
        return tipoActivoService.guardarTipoActivo(tipoActivo);
    }

    // Eliminar un tipo de activo por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoActivo(@PathVariable Integer id) {
        tipoActivoService.eliminarTipoActivo(id);
        return ResponseEntity.noContent().build(); // 204 sin body
    }

}
