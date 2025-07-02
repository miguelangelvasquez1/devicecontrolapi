package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.Activo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ActivoRepository extends JpaRepository<Activo, Integer> {

    //Consultas CRUD se hacen automáticamente

    // List<Activo> findAll(); //Redundante?, solo definirlo en service y controller

    boolean existsBySerial(String serial);

    // Contar activos por tipo usando JPQL
    @Query("SELECT COUNT(a) FROM Activo a WHERE a.tipoActivo.id = :idTipoActivo")
    long countActivosPorTipo(@Param("idTipoActivo") Integer idTipoActivo);

    // Buscar activos cuyo nombre contenga una palabra
    @Query("SELECT a FROM Activo a WHERE a.nombre LIKE %:nombre%")
    List<Activo> buscarPorNombre(@Param("nombre") String nombre);

    // Buscar activos por estado y tipo de activo
    @Query("SELECT a FROM Activo a WHERE a.estado = :estado AND a.tipoActivo.id = :idTipoActivo")
    List<Activo> buscarPorEstadoYTipo(@Param("estado") char estado, @Param("idTipoActivo") Integer idTipoActivo);
}