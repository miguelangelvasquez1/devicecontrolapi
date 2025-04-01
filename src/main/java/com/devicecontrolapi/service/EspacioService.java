package com.devicecontrolapi.service;

import com.devicecontrolapi.model.Espacio;
import com.devicecontrolapi.repository.EspacioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EspacioService {

    @Autowired
    private EspacioRepository espacioRepository;

    // Registrar o actualizar un espacio
    public Espacio guardarEspacio(Espacio espacio) {
        return espacioRepository.save(espacio);
    }

    // Obtener todos los espacios
    public List<Espacio> obtenerTodosLosEspacios() {
        return espacioRepository.findAll();
    }

    // Obtener un espacio por su ID
    public Optional<Espacio> obtenerEspacioPorId(Integer id) {
        return espacioRepository.findById(id);
    }

    // Buscar espacios por nombre
    public List<Espacio> buscarEspaciosPorNombre(String nombre) {
        return espacioRepository.buscarPorNombre(nombre);
    }

    // Contar el total de espacios
    public long contarEspacios() {
        return espacioRepository.contarEspacios();
    }

    // Eliminar un espacio
    public void eliminarEspacio(Integer id) {
        espacioRepository.deleteById(id);
    }
}
