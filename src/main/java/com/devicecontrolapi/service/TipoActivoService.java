package com.devicecontrolapi.service;

import com.devicecontrolapi.model.TipoActivo;
import com.devicecontrolapi.repository.TipoActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoActivoService {

    @Autowired
    private TipoActivoRepository tipoActivoRepository;

    // Registrar o actualizar un tipo de activo
    public TipoActivo guardarTipoActivo(TipoActivo tipoActivo) {
        return tipoActivoRepository.save(tipoActivo);
    }

    // Obtener todos los tipos de activos
    public List<TipoActivo> obtenerTodosLosTiposDeActivos() {
        return tipoActivoRepository.findAll();
    }

    // Obtener un tipo de activo por ID
    public Optional<TipoActivo> obtenerTipoActivoPorId(Integer id) {
        return tipoActivoRepository.findById(id);
    }

    // Buscar tipos de activos por nombre
    public List<TipoActivo> buscarPorNombre(String nombre) {
        return tipoActivoRepository.buscarPorNombre(nombre);
    }

    // Contar el total de tipos de activos
    public long contarTiposDeActivos() {
        return tipoActivoRepository.contarTiposDeActivos();
    }

    // Verificar si un tipo de activo ya existe por su nombre
    public boolean existeTipoActivoConNombre(String nombre) {
        return tipoActivoRepository.existeTipoActivoConNombre(nombre);
    }

    // Eliminar un tipo de activo
    public void eliminarTipoActivo(Integer id) {
        tipoActivoRepository.deleteById(id);
    }
}
