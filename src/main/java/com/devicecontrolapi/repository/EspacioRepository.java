package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EspacioRepository extends JpaRepository<Espacio, Integer> {

    // Buscar espacios cuyo nombre contenga una palabra clave
    @Query("SELECT e FROM Espacio e WHERE e.nombre LIKE %:nombre%")
    List<Espacio> buscarPorNombre(@Param("nombre") String nombre);

    // Contar espacios registrados
    @Query("SELECT COUNT(e) FROM Espacio e")
    long contarEspacios();
}
