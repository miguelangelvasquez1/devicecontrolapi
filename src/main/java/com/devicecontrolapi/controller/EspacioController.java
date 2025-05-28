package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.service.EspacioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/espacios")
public class EspacioController {

    @Autowired
    private EspacioService espacioService;

    // Obtener todos los espacios
    @GetMapping
    public List<Espacio> obtenerTodosLosEspacios() {
        return espacioService.obtenerTodosLosEspacios();
    }

    // Obtener un espacio por ID
    @GetMapping("/{id}")
    public Optional<Espacio> obtenerEspacioPorId(@PathVariable Integer id) {
        return espacioService.obtenerEspacioPorId(id);
    }

    // Buscar espacios por nombre
    @GetMapping("/buscarPorNombre")
    public List<Espacio> buscarEspaciosPorNombre(@RequestParam String nombre) {
        return espacioService.buscarEspaciosPorNombre(nombre);
    }

    // Contar espacios registrados
    @GetMapping("/contar")
    public long contarEspacios() {
        return espacioService.contarEspacios();
    }

    // Crear un nuevo espacio
    @PostMapping
    public ResponseEntity<Espacio> crearEspacio(@RequestBody Espacio espacio) {
        Espacio nuevoEspacio = espacioService.crearEspacio(espacio);
        return ResponseEntity.ok(nuevoEspacio);
    }

    // Actualizar un espacio existente
    @PutMapping("/{id}")
    public ResponseEntity<Espacio> actualizarEspacio(@PathVariable("id") Integer id, @RequestBody Espacio espacio) {
        Espacio espacioActualizado = espacioService.actualizarEspacio(id, espacio);
        return ResponseEntity.ok(espacioActualizado);
    }

    // Eliminar un espacio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEspacio(@PathVariable("id") Integer id) {
        espacioService.eliminarEspacio(id);
        return ResponseEntity.noContent().build();
    }
}