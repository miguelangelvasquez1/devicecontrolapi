package com.devicecontrolapi.service;

import com.devicecontrolapi.model.Activo;
import com.devicecontrolapi.repository.ActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    // Método para agregar un activo
    public Activo agregarActivo(Activo activo) {
        return activoRepository.save(activo);
    }

    // Método para actualizar un activo, o hacer consulta de MYSQL
    public Activo actualizarActivo(Integer id, Activo activoActualizado) {
        Optional<Activo> activoExistente = activoRepository.findById(id);
        if (activoExistente.isPresent()) {
            Activo activo = activoExistente.get();
            activo.setNombre(activoActualizado.getNombre());
            activo.setEstado(activoActualizado.getEstado());
            activo.setTipoActivo(activoActualizado.getTipoActivo());
            // Aquí puedes agregar más campos si lo necesitas.
            return activoRepository.save(activo);
        } else {
            return null;  // O lanzar una excepción si no se encuentra el activo
        }
    }

    // Método para eliminar un activo
    public boolean eliminarActivo(Integer id) {
        Optional<Activo> activoExistente = activoRepository.findById(id);
        if (activoExistente.isPresent()) {
            activoRepository.deleteById(id);
            return true;
        } else {
            return false;  // O lanzar una excepción si no se encuentra el activo
        }
    }
}