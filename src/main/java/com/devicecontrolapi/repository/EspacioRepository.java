package com.devicecontrolapi.repository;

import com.devicecontrolapi.model.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EspacioRepository extends JpaRepository<Espacio, Integer> {

    // Buscar espacios cuyo nombre contenga una palabra clave (solo no eliminados)
    @Query("SELECT e FROM Espacio e WHERE e.eliminado = 0 AND LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Espacio> buscarPorNombre(@Param("nombre") String nombre);

    // Contar espacios registrados (solo no eliminados)
    @Query("SELECT COUNT(e) FROM Espacio e WHERE e.eliminado = 0")
    long contarEspacios();

    // Buscar espacios cuyo nombre contenga una palabra clave (solo no eliminados)
    @Query("SELECT e FROM Espacio e WHERE e.eliminado = 0 AND LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Espacio> buscarPorNombreNoEliminados(@Param("nombre") String nombre);

    // Contar espacios no eliminados
    long countByEliminado(Integer eliminado);

    List<Espacio> findAllByEliminado(Integer eliminado);

    Optional<Espacio> findByIdespacioAndEliminado(Integer idespacio, Integer eliminado);
}