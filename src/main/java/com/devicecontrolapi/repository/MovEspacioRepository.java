package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.MovEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovEspacioRepository extends JpaRepository<MovEspacio, Integer> {

    // Buscar movimientos por espacio
    @Query("SELECT m FROM MovEspacio m WHERE m.espacio.idespacio = :idEspacio")
    List<MovEspacio> buscarPorEspacio(@Param("idEspacio") Integer idEspacio);

    // Buscar movimientos activos (sin devolución)
    @Query("SELECT m FROM MovEspacio m WHERE m.fechaDevol IS NULL")
    List<MovEspacio> buscarMovimientosActivos();

    // Buscar movimientos por usuario
    @Query("SELECT m FROM MovEspacio m WHERE m.usuario.id = :idUsuario")
    List<MovEspacio> buscarPorUsuario(@Param("idUsuario") Integer idUsuario);

    // Buscar movimientos en un rango de fechas
    @Query("SELECT m FROM MovEspacio m WHERE m.fechaPres BETWEEN :fechaInicio AND :fechaFin")
    List<MovEspacio> buscarPorRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
