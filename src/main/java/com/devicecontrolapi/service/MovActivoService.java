package com.devicecontrolapi.service;

import com.devicecontrolapi.model.MovActivo;
import com.devicecontrolapi.repository.MovActivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovActivoService {

    @Autowired
    private MovActivoRepository movActivoRepository;

    // Registrar o actualizar un movimiento de activo
    public MovActivo guardarMovActivo(MovActivo movActivo) {
        return movActivoRepository.save(movActivo);
    }

    // Obtener todos los movimientos de activos
    public List<MovActivo> obtenerTodosLosMovActivos() {
        return movActivoRepository.findAll();
    }

    // Obtener un movimiento por su ID
    public Optional<MovActivo> obtenerMovActivoPorId(Integer id) {
        return movActivoRepository.findById(id);
    }

    // Buscar movimientos por estado
    public List<MovActivo> buscarPorEstado(char estado) {
        return movActivoRepository.buscarPorEstado(estado);
    }

    // Buscar movimientos por activo
    public List<MovActivo> buscarPorActivo(Integer idActivo) {
        return movActivoRepository.buscarPorActivo(idActivo);
    }

    // Buscar movimientos dentro de un rango de fechas
    public List<MovActivo> buscarPorRangoFechas(Date fechaInicio, Date fechaFin) {
        return movActivoRepository.buscarPorRangoFechas(fechaInicio, fechaFin);
    }

    // Eliminar un movimiento de activo
    public void eliminarMovActivo(Integer id) {
        movActivoRepository.deleteById(id);
    }
}
