package com.devicecontrolapi.service;

import com.devicecontrolapi.model.Activo;
import com.devicecontrolapi.repository.ActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivoService {

    // Inyectar el repositorio
    @Autowired
    private ActivoRepository activoRepository;

    // Método para contar los activos por tipo
    public long contarActivosPorTipo(Integer idTipoActivo) {
        return activoRepository.countActivosPorTipo(idTipoActivo);
    }

    // Método para buscar activos por nombre (búsqueda parcial)
    public List<Activo> buscarActivosPorNombre(String nombre) {
        return activoRepository.buscarPorNombre(nombre);
    }

    // Método para buscar activos por estado y tipo de activo
    public List<Activo> buscarActivosPorEstadoYTipo(char estado, Integer idTipoActivo) {
        return activoRepository.buscarPorEstadoYTipo(estado, idTipoActivo);
    }

    // Puedes agregar otros métodos que necesites
}