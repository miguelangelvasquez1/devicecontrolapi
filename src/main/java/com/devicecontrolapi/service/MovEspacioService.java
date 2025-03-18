package com.devicecontrolapi.service;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.repository.MovEspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MovEspacioService {

    @Autowired
    private MovEspacioRepository movEspacioRepository;

    // Registrar o actualizar un movimiento de espacio
    public MovEspacio guardarMovEspacio(MovEspacio movEspacio) {
        return movEspacioRepository.save(movEspacio);
    }

    // Obtener todos los movimientos de espacios
    public List<MovEspacio> obtenerTodosLosMovEspacios() {
        return movEspacioRepository.findAll();
    }

    // Obtener un movimiento de espacio por ID
    public Optional<MovEspacio> obtenerMovEspacioPorId(Integer id) {
        return movEspacioRepository.findById(id);
    }

    // Buscar movimientos por espacio
    public List<MovEspacio> buscarPorEspacio(Integer idEspacio) {
        return movEspacioRepository.buscarPorEspacio(idEspacio);
    }

    // Buscar movimientos activos (sin devolución)
    public List<MovEspacio> buscarMovimientosActivos() {
        return movEspacioRepository.buscarMovimientosActivos();
    }

    // Buscar movimientos por usuario
    public List<MovEspacio> buscarPorUsuario(Integer idUsuario) {
        return movEspacioRepository.buscarPorUsuario(idUsuario);
    }

    // Buscar movimientos dentro de un rango de fechas
    public List<MovEspacio> buscarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return movEspacioRepository.buscarPorRangoFechas(fechaInicio, fechaFin);
    }

    // Eliminar un movimiento de espacio
    public void eliminarMovEspacio(Integer id) {
        movEspacioRepository.deleteById(id);
    }
}
