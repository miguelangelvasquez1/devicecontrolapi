package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.MovActivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface MovActivoRepository extends JpaRepository<MovActivo, Integer> {

    // Buscar movimientos por estado
    @Query("SELECT m FROM MovActivo m WHERE m.estado = :estado")
    List<MovActivo> buscarPorEstado(@Param("estado") char estado);

    // Buscar movimientos por activo
    @Query("SELECT m FROM MovActivo m WHERE m.activo.id = :idActivo")
    List<MovActivo> buscarPorActivo(@Param("idActivo") Integer idActivo);

    // Buscar movimientos en un rango de fechas
    @Query("SELECT m FROM MovActivo m WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<MovActivo> buscarPorRangoFechas(@Param("fechaInicio") Date fechaInicio, @Param("fechaFin") Date fechaFin);
}
