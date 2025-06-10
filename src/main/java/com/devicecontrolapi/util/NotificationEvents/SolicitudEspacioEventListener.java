package com.devicecontrolapi.util.NotificationEvents;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.devicecontrolapi.service.NotificacionService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolicitudEspacioEventListener {
    
    private final NotificacionService notificacionService;
    
    @EventListener
    @Async
    public void handleSolicitudEspacioEvent(SolicitudEspacioEvent event) {
        log.info("Procesando evento de solicitud: {} - {}", 
                event.getSolicitud().getIdmov(), 
                event.getTipoNotificacion());
        
        notificacionService.crearNotificacionSolicitudEspacio(
                event.getSolicitud(), 
                event.getTipoNotificacion()
        );
    }
}
