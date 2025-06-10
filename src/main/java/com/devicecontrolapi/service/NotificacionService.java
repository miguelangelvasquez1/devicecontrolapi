package com.devicecontrolapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.model.Notificacion;
import com.devicecontrolapi.model.Usuario;
import com.devicecontrolapi.repository.NotificacionRepository;
import com.devicecontrolapi.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // Para WebSocket

    /**
     * Obtiene el usuario actual autenticado
     */
    private Usuario getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof String email) {
                log.info(email + " autenticado");
                return userRepository.findByEmail(email);
            }
        }

        throw new IllegalStateException("Usuario no autenticado");
    }

    /**
     * Crea una nueva notificación
     */
    public Notificacion crearNotificacion(Notificacion notificacion) {
        Notificacion saved = notificacionRepository.save(notificacion);

        // Enviar notificación en tiempo real via WebSocket
        enviarNotificacionWebSocket(saved);

        return saved;
    }

    /**
     * Crea una notificación para una solicitud de espacio
     */
    @Async
    public void crearNotificacionSolicitudEspacio(MovEspacio solicitud, Notificacion.TipoNotificacion tipo) {
        String titulo = "";
        String mensaje = "";

        switch (tipo) {
            case Notificacion.TipoNotificacion.SOLICITUD_CREADA:
                titulo = "Nueva Solicitud de Espacio";
                mensaje = String.format("%s ha solicitado el espacio %s para el %s",
                        solicitud.getUsuario().getNombre(),
                        solicitud.getEspacio().getNombre(),
                        solicitud.getFechaPres());

                crearNotificacionParaVigilantes(titulo, mensaje, solicitud, tipo);
                break;

            case Notificacion.TipoNotificacion.SOLICITUD_CANCELADA:
                titulo = "Solicitud Cancelada";
                mensaje = String.format("%s ha cancelado su solicitud para el espacio %s",
                        solicitud.getUsuario().getNombre(),
                        solicitud.getEspacio().getNombre());

                crearNotificacionParaVigilantes(titulo, mensaje, solicitud, tipo);
                break;

            case Notificacion.TipoNotificacion.SOLICITUD_APROBADA:
                titulo = "Solicitud Aprobada";
                mensaje = String.format("Tu solicitud para el espacio %s ha sido aprobada",
                        solicitud.getEspacio().getNombre());

                crearNotificacion(
                        Notificacion.builder()
                                .titulo(titulo)
                                .mensaje(mensaje)
                                .tipo(tipo)
                                .usuario(solicitud.getUsuario())
                                .solicitudId((long) solicitud.getIdmov())
                                .urlRedireccion("/solicitudes?id=" + solicitud.getIdmov())
                                .build());
                break;

            case Notificacion.TipoNotificacion.SOLICITUD_RECHAZADA:
                titulo = "Solicitud Rechazada";
                mensaje = String.format("Tu solicitud para el espacio %s ha sido rechazada",
                        solicitud.getEspacio().getNombre());

                crearNotificacion(
                        Notificacion.builder()
                                .titulo(titulo)
                                .mensaje(mensaje)
                                .tipo(tipo)
                                .usuario(solicitud.getUsuario())
                                .solicitudId((long) solicitud.getIdmov())
                                .urlRedireccion("/solicitudes?id=" + solicitud.getIdmov())
                                .build());
                break;
            default:
                break;
        }
    }

    /**
     * Obtiene todas las notificaciones del usuario actual
     */
    public List<Notificacion> obtenerMisNotificaciones() {
        Usuario usuario = getCurrentUser();
        return notificacionRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    /**
     * Obtiene notificaciones con paginación
     */
    public Page<Notificacion> obtenerMisNotificacionesPaginadas(int page, int size) {
        Usuario usuario = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return notificacionRepository.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable);
    }

    /**
     * Obtiene solo las notificaciones no leídas
     */
    public List<Notificacion> obtenerNotificacionesNoLeidas() {
        Usuario usuario = getCurrentUser();
        return notificacionRepository.findByUsuarioAndLeidaFalseOrderByFechaCreacionDesc(usuario);
    }

    /**
     * Obtiene el número de notificaciones no leídas
     */
    public long contarNotificacionesNoLeidas() {
        Usuario usuario = getCurrentUser();
        return notificacionRepository.countByUsuarioAndLeidaFalse(usuario);
    }

    /**
     * Marca una notificación como leída
     */
    public Notificacion marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación pertenece al usuario actual
        if (!notificacion.getUsuario().equals(getCurrentUser())) {
            throw new IllegalStateException("No tienes permiso para modificar esta notificación");
        }

        notificacion.marcarComoLeida();
        return notificacionRepository.save(notificacion);
    }

    /**
     * Marca todas las notificaciones como leídas
     */
    public void marcarTodasComoLeidas() {
        Usuario usuario = getCurrentUser();
        notificacionRepository.marcarTodasComoLeidas(usuario, LocalDateTime.now());
    }

    /**
     * Elimina una notificación
     */
    public void eliminarNotificacion(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));

        // Verificar que la notificación pertenece al usuario actual
        if (!notificacion.getUsuario().equals(getCurrentUser())) {
            throw new IllegalStateException("No tienes permiso para eliminar esta notificación");
        }

        notificacionRepository.deleteById(notificacionId);
    }

    /**
     * Busca notificaciones por texto
     */
    public List<Notificacion> buscarNotificaciones(String texto) {
        Usuario usuario = getCurrentUser();
        return notificacionRepository.buscarPorTexto(usuario, texto);
    }

    /**
     * Envía notificación en tiempo real via WebSocket
     */
    private void enviarNotificacionWebSocket(Notificacion notificacion) {
        try {
            String destination = "/user/" + notificacion.getUsuario().getEmail() + "/notificaciones";
            messagingTemplate.convertAndSend(destination, notificacion);
        } catch (Exception e) {
            log.error("Error al enviar notificación por WebSocket", e);
        }
    }

    // Para enviar múltiples notificaciones a vigilantes
    private List<Notificacion> crearNotificacionParaVigilantes(String titulo, String mensaje, MovEspacio solicitud,
            Notificacion.TipoNotificacion tipo) {
        List<Usuario> vigilantes = obtenerListaVigilantes();
        if (vigilantes.isEmpty()) {
            log.warn("No hay vigilantes registrados para enviar notificación");
            return List.of();
        }

        return vigilantes.stream().map(vigilante -> {
            Notificacion notificacion = Notificacion.builder()
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .usuario(vigilante)
                    .solicitudId((long) solicitud.getIdmov())
                    .urlRedireccion("/solicitudes?id=" + solicitud.getIdmov())
                    .build();
            return crearNotificacion(notificacion);
        }).toList();
    }

    /**
     * Obtiene la lista de vigilantes
     */
    private List<Usuario> obtenerListaVigilantes() {
        return userRepository.findByRol((byte) 1); //Rol de vigilante
    }

    /**
     * Limpia notificaciones antiguas (ejecutar cada día a las 2 AM)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void limpiarNotificacionesAntiguas() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(30);
        int eliminadas = notificacionRepository.eliminarNotificacionesAntiguas(fechaLimite);
        log.info("Se eliminaron {} notificaciones antiguas", eliminadas);
    }

    /**
     * Crea notificaciones recordatorio para solicitudes próximas
     */
    @Scheduled(cron = "0 0 9 * * ?") // Cada día a las 9 AM
    public void crearNotificacionesRecordatorio() {
        // Implementar lógica para recordatorios de solicitudes próximas
        log.info("Verificando solicitudes próximas para recordatorios");
    }
}