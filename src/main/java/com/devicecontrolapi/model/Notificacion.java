package com.devicecontrolapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(name = "tipo_notificacion", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idusuario", nullable = false) //Usuario al que le llega
    private Usuario usuario;

    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_lectura")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaLectura;

    @Column(name = "leida")
    @Builder.Default
    private boolean leida = false;

    @Column(name = "solicitud_id")
    private Long solicitudId;

    @Column(name = "url_redireccion")
    private String urlRedireccion;

    @Column(name = "icono")
    private String icono;

    @Column(name = "color")
    private String color;

    @PrePersist
    public void prePersist() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (tipo == null) {
            tipo = TipoNotificacion.INFORMACION;
        }
        
        // Asignar icono y color según el tipo
        switch (tipo) {
            case SOLICITUD_CREADA:
                icono = "fa-plus-circle";
                color = "#4CAF50";
                break;
            case SOLICITUD_APROBADA:
                icono = "fa-check-circle";
                color = "#4CAF50";
                break;
            case SOLICITUD_RECHAZADA:
                icono = "fa-times-circle";
                color = "#F44336";
                break;
            case SOLICITUD_CANCELADA:
                icono = "fa-ban";
                color = "#FF9800";
                break;
            case SOLICITUD_PROXIMA:
                icono = "fa-clock";
                color = "#2196F3";
                break;
            default:
                icono = "fa-info-circle";
                color = "#6096BA";
        }
    }

    public enum TipoNotificacion {
        SOLICITUD_CREADA,
        SOLICITUD_APROBADA,
        SOLICITUD_RECHAZADA,
        SOLICITUD_CANCELADA,
        SOLICITUD_PROXIMA,
        INFORMACION,
        ALERTA,
        SISTEMA
    }

    // Método para marcar como leída
    public void marcarComoLeida() {
        this.leida = true;
        this.fechaLectura = LocalDateTime.now();
    }
}