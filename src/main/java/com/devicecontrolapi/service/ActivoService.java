package com.devicecontrolapi.service;

import com.devicecontrolapi.exceptions.DuplicateSerialException;
import com.devicecontrolapi.model.Activo;
import com.devicecontrolapi.repository.ActivoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivoService {

    // Inyectar el repositorio
    @Autowired
    private ActivoRepository activoRepository;

    public List<Activo> findAll() {
        return activoRepository.findAll();
    }

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

        if (!isSerialAvailable(activo.getSerial())) {
            throw new DuplicateSerialException();
        }

        return activoRepository.save(activo);
    }

    // Método para actualizar un activo
    public Activo actualizarActivo(Integer id, Activo activoActualizado) {

        // Buscar el activo existente
        Optional<Activo> activoExistente = activoRepository.findById(id);

        if (activoExistente.isPresent()) {

            Activo activo = activoExistente.get();

            // Verificar si el serial ha cambiado
            if (!activoActualizado.getSerial().equals(activo.getSerial())) {

                // Si el serial ha cambiado, verificar si el nuevo serial ya está en uso
                if (!isSerialAvailable(activoActualizado.getSerial())) {
                    throw new DuplicateSerialException();
                }
            }

            // Actualizar los campos del activo
            activo.setNombre(activoActualizado.getNombre());
            activo.setEstado(activoActualizado.getEstado());
            activo.setTipoActivo(activoActualizado.getTipoActivo());
            activo.setEspacio(activoActualizado.getEspacio());
            activo.setUrl(activoActualizado.getUrl());
            activo.setSerial(activoActualizado.getSerial());
            activo.setObservaciones(activoActualizado.getObservaciones());

            // Guardar el activo actualizado
            return activoRepository.save(activo);

        } else {
            // Si el activo no existe, podrías lanzar una excepción o devolver un valor
            // apropiado
            throw new EntityNotFoundException("Activo no encontrado con ID: " + id);
        }
    }

    // Método para eliminar un activo
    public boolean eliminarActivo(Integer id) {
        Optional<Activo> activoExistente = activoRepository.findById(id);
        if (activoExistente.isPresent()) {
            activoRepository.deleteById(id);
            return true;
        } else {
            return false; // O lanzar una excepción si no se encuentra el activo
        }
    }

    public boolean isSerialAvailable(String serial) {
        return !activoRepository.existsBySerial(serial);
    }

}