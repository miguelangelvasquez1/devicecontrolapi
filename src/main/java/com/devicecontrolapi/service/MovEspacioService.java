package com.devicecontrolapi.service;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.model.Notificacion;
import com.devicecontrolapi.repository.MovEspacioRepository;
import com.devicecontrolapi.util.NotificationEvents.SolicitudEspacioEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MovEspacioService {

    @Autowired
    private MovEspacioRepository movEspacioRepository;
    private final ApplicationEventPublisher eventPublisher; // Para las notificaciones

    // Registrar o actualizar un movimiento de espacio
    public MovEspacio guardarMovEspacio(MovEspacio movEspacio) {
        if (!verificarDisponibilidad(movEspacio.getEspacio().getIdespacio(),
                movEspacio.getFechaPres(),
                movEspacio.getFechaDevol())) {
            throw new IllegalStateException("El espacio no está disponible en el horario solicitado");
        }

        MovEspacio saved = movEspacioRepository.save(movEspacio);

        // Publicar evento para crear notificación
        eventPublisher.publishEvent(new SolicitudEspacioEvent(
                this, saved, Notificacion.TipoNotificacion.SOLICITUD_CREADA));

        return saved;
    }

    // método aprobarSolicitud
    @Transactional
    public MovEspacio aprobarSolicitud(Integer id) {
        MovEspacio solicitud = movEspacioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        if (!"Pendiente".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
        }

        solicitud.setEstado("Aprobada");
        MovEspacio saved = movEspacioRepository.save(solicitud);

        // Publicar evento para crear notificación
        eventPublisher.publishEvent(new SolicitudEspacioEvent(
                this, saved, Notificacion.TipoNotificacion.SOLICITUD_APROBADA));

        return saved;
    }

    // método rechazarSolicitud
    @Transactional
    public MovEspacio rechazarSolicitud(Integer id) {
        MovEspacio solicitud = movEspacioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        if (!"Pendiente".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
        }

        solicitud.setEstado("Rechazada");
        MovEspacio saved = movEspacioRepository.save(solicitud);

        // Publicar evento para crear notificación
        eventPublisher.publishEvent(new SolicitudEspacioEvent(
                this, saved, Notificacion.TipoNotificacion.SOLICITUD_RECHAZADA));

        return saved;
    }

    // método cancelarSolicitud
    public void cancelarSolicitud(Integer id) {
        MovEspacio solicitud = movEspacioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con ID: " + id));

        if (!"Pendiente".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden cancelar solicitudes pendientes");
        }

        solicitud.setEstado("Cancelada");
        MovEspacio saved = movEspacioRepository.save(solicitud);

        // Publicar evento para crear notificación
        eventPublisher.publishEvent(new SolicitudEspacioEvent(
                this, saved, Notificacion.TipoNotificacion.SOLICITUD_CANCELADA));
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

    public boolean verificarDisponibilidad(Integer idEspacio, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovEspacio> reservasExistentes = movEspacioRepository
                .findByEspacioIdespacioAndFechaDevolAfterAndFechaPresBefore(
                        idEspacio, fechaInicio, fechaFin);

        return reservasExistentes.isEmpty();
    }

}
