package com.devicecontrolapi.service;

import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.repository.EspacioRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspacioService {

    @Autowired
    private EspacioRepository espacioRepository;

// Crear un nuevo espacio
    public Espacio crearEspacio(Espacio espacio) {
        // Validar que el objeto Espacio no sea nulo
        if (espacio == null) {
            throw new IllegalArgumentException("El objeto Espacio no puede ser nulo");
        }

        // Asegurarse de que no se proporcione un idespacio para nuevos espacios
        if (espacio.getIdespacio() != null) {
            throw new IllegalArgumentException("No se debe proporcionar un idespacio para crear un nuevo espacio");
        }

        // Establecer eliminado = 0 para nuevos espacios
        espacio.setEliminado(0);

        // Verificar si ya existe un espacio con el mismo nombre
        List<Espacio> espaciosConMismoNombre = espacioRepository.buscarPorNombreNoEliminados(espacio.getNombre());
        if (!espaciosConMismoNombre.isEmpty()) {
            throw new IllegalArgumentException("Ya existe un espacio con el nombre: " + espacio.getNombre());
        }

        // Crear el nuevo espacio
        try {
            return espacioRepository.save(espacio);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el espacio: " + e.getMessage(), e);
        }
    }

    // Actualizar un espacio existente
    public Espacio actualizarEspacio(Integer idespacio, Espacio espacio) {
        // Validar que el objeto Espacio no sea nulo
        if (espacio == null) {
            throw new IllegalArgumentException("El objeto Espacio no puede ser nulo");
        }

        // Verificar que el espacio existe y no está eliminado
        Optional<Espacio> espacioExistente = espacioRepository.findByIdespacioAndEliminado(idespacio, 0);
        if (!espacioExistente.isPresent()) {
            throw new EntityNotFoundException("Espacio no encontrado con id: " + idespacio + " o está eliminado");
        }

        // Verificar si el nombre está en uso por OTRO espacio
        List<Espacio> espaciosConMismoNombre = espacioRepository.buscarPorNombreNoEliminados(espacio.getNombre());
        if (!espaciosConMismoNombre.isEmpty()) {
            Espacio espacioConMismoNombre = espaciosConMismoNombre.get(0);
            if (!espacioConMismoNombre.getIdespacio().equals(idespacio)) {
                throw new IllegalArgumentException("Ya existe otro espacio con el nombre: " + espacio.getNombre());
            }
        }

        // Actualizar el espacio existente
        Espacio espacioActual = espacioExistente.get();
        espacioActual.setNombre(espacio.getNombre());
        espacioActual.setDescripcion(espacio.getDescripcion());
        espacioActual.setEstado(espacio.getEstado());
        espacioActual.setCapacidad(espacio.getCapacidad());
        espacioActual.setEliminado(espacio.getEliminado() != null ? espacio.getEliminado() : 0);

        try {
            return espacioRepository.save(espacioActual);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el espacio: " + e.getMessage(), e);
        }
    }

    // Obtener todos los espacios (solo los no eliminados)
    public List<Espacio> obtenerTodosLosEspacios() {
        return espacioRepository.findAllByEliminado(0);
    }

    // Obtener un espacio por su ID (solo si no está eliminado)
    public Optional<Espacio> obtenerEspacioPorId(Integer id) {
        return espacioRepository.findByIdespacioAndEliminado(id, 0);
    }

    // Buscar espacios por nombre (solo los no eliminados)
    public List<Espacio> buscarEspaciosPorNombre(String nombre) {
        return espacioRepository.buscarPorNombreNoEliminados(nombre);
    }

    // Contar el total de espacios no eliminados
    public long contarEspacios() {
        return espacioRepository.countByEliminado(0);
    }

    // Eliminado lógico de un espacio
    public void eliminarEspacio(Integer id) {
        Espacio espacio = espacioRepository.findByIdespacioAndEliminado(id, 0)
            .orElseThrow(() -> new EntityNotFoundException("Espacio no encontrado con id: " + id));
        
        espacio.setEliminado(1);
        espacioRepository.save(espacio);
    }
}