package com.devicecontrolapi.controller;

import com.devicecontrolapi.exceptions.DuplicateSerialException;
import com.devicecontrolapi.model.Activo;
import com.devicecontrolapi.service.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activos")
public class ActivoController {

    @Autowired
    private ActivoService activoService;

    @GetMapping()
    public List<Activo> findAll() {
        return activoService.findAll();
    }

    // Endpoint para contar los activos por tipo
    @GetMapping("/contar/{idTipoActivo}")
    public long contarActivosPorTipo(@PathVariable Integer idTipoActivo) {
        return activoService.contarActivosPorTipo(idTipoActivo);
    }

    // Endpoint para buscar activos por nombre
    @GetMapping("/buscarPorNombre")
    public List<Activo> buscarActivosPorNombre(@RequestParam String nombre) {
        return activoService.buscarActivosPorNombre(nombre);
    }

    // Endpoint para buscar activos por estado y tipo
    @GetMapping("/buscarPorEstadoYTipo")
    public List<Activo> buscarActivosPorEstadoYTipo(
            @RequestParam char estado, @RequestParam Integer idTipoActivo) {
        return activoService.buscarActivosPorEstadoYTipo(estado, idTipoActivo);
    }

    // Endpoint para agregar un activo
    @PostMapping()
    public ResponseEntity<?> agregarActivo(@RequestBody Activo activo) {
        try {
            return ResponseEntity.ok(activoService.agregarActivo(activo));
        } catch (DuplicateSerialException e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody); // Devolverá JSON: { "message": "..." }
        }
    }

    // Endpoint para actualizar un activo
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarActivo(@PathVariable("id") Integer id, @RequestBody Activo activo) {

        try {
            Activo activoActualizado = activoService.actualizarActivo(id, activo);
            if (activoActualizado != null) {
                return ResponseEntity.ok(activoActualizado);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (DuplicateSerialException e) {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorBody); // Devolverá JSON: { "message": "..." }
        }
    }

    // Endpoint para eliminar un activo
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarActivo(@PathVariable("id") Integer id) {
        boolean eliminado = activoService.eliminarActivo(id);
        if (eliminado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
