package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.TipoActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TipoActivoRepository extends JpaRepository<TipoActivo, Integer> {

    // Buscar tipos de activos cuyo nombre contenga una palabra clave
    @Query("SELECT t FROM TipoActivo t WHERE t.nombre LIKE %:nombre%")
    List<TipoActivo> buscarPorNombre(@Param("nombre") String nombre);

    // Contar cuántos tipos de activos hay registrados
    @Query("SELECT COUNT(t) FROM TipoActivo t")
    long contarTiposDeActivos();

    // Verificar si existe un tipo de activo con un nombre específico
    @Query("SELECT COUNT(t) > 0 FROM TipoActivo t WHERE t.nombre = :nombre")
    boolean existeTipoActivoConNombre(@Param("nombre") String nombre);
}
