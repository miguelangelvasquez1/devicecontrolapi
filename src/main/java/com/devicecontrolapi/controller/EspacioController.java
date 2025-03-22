package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.service.EspacioService;
import org.springframework.beans.factory.annotation.Autowired;
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

    // Obtener un espacio por ID, OPCIONAL?
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

    // Crear o actualizar un espacio
    @PostMapping
    public Espacio guardarEspacio(@RequestBody Espacio espacio) {
        return espacioService.guardarEspacio(espacio);
    }

    // Eliminar un espacio por ID
    @DeleteMapping("/{id}")
    public void eliminarEspacio(@PathVariable Integer id) {
        espacioService.eliminarEspacio(id);
    }
}
