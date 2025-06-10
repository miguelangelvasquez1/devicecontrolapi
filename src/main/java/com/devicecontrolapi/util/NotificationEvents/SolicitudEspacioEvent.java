package com.devicecontrolapi.util.NotificationEvents;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import com.devicecontrolapi.model.MovEspacio;
import com.devicecontrolapi.model.Notificacion;

@Getter
public class SolicitudEspacioEvent extends ApplicationEvent {
    
    private final MovEspacio solicitud;
    private final Notificacion.TipoNotificacion tipoNotificacion;
    
    public SolicitudEspacioEvent(Object source, MovEspacio solicitud, 
                                Notificacion.TipoNotificacion tipoNotificacion) {
        super(source);
        this.solicitud = solicitud;
        this.tipoNotificacion = tipoNotificacion;
    }
}