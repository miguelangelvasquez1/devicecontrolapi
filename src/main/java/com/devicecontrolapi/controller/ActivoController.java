package com.devicecontrolapi.controller;

import com.devicecontrolapi.model.Activo;
import com.devicecontrolapi.service.ActivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activos")
public class ActivoController {

    @Autowired
    private ActivoService activoService;

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
}
