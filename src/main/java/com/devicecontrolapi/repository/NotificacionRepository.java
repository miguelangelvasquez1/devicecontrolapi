package com.devicecontrolapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.devicecontrolapi.model.Notificacion;
import com.devicecontrolapi.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Encuentra todas las notificaciones de un usuario ordenadas por fecha descendente
     */
    List<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    /**
     * Encuentra notificaciones no leídas de un usuario
     */
    List<Notificacion> findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(Usuario usuario);

    /**
     * Cuenta las notificaciones no leídas de un usuario
     */
    long countByUsuarioAndLeidaFalse(Usuario usuario);

    /**
     * Encuentra notificaciones de un usuario con paginación
     */
    Page<Notificacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);

    /**
     * Encuentra notificaciones por tipo
     */
    List<Notificacion> findByUsuarioAndTipoOrderByFechaCreacionDesc(
            Usuario usuario, 
            Notificacion.TipoNotificacion tipo
    );

    /**
     * Marca todas las notificaciones de un usuario como leídas
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = :fecha " +
           "WHERE n.usuario = :usuario AND n.leida = false")
    int marcarTodasComoLeidas(@Param("usuario") Usuario usuario, @Param("fecha") LocalDateTime fecha);

    /**
     * Elimina notificaciones antiguas
     */
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.fechaCreacion < :fecha")
    int eliminarNotificacionesAntiguas(@Param("fecha") LocalDateTime fecha);

    /**
     * Encuentra notificaciones relacionadas con una solicitud
     */
    List<Notificacion> findBySolicitudIdOrderByFechaCreacionDesc(Long solicitudId);

    /**
     * Encuentra las últimas N notificaciones de un usuario
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario = :usuario " +
           "ORDER BY n.fechaCreacion DESC")
    List<Notificacion> findUltimasNotificaciones(@Param("usuario") Usuario usuario, Pageable pageable);

    /**
     * Busca notificaciones por texto
     */
    @Query("SELECT n FROM Notificacion n WHERE n.usuario = :usuario " +
           "AND (LOWER(n.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(n.mensaje) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "ORDER BY n.fechaCreacion DESC")
    List<Notificacion> buscarPorTexto(@Param("usuario") Usuario usuario, @Param("texto") String texto);
}