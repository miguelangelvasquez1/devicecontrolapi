package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mov_espacios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmov;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaPres;
    private LocalDateTime fechaDevol;
    private String estado;
    private String motivo;  

    @ManyToOne
    @JoinColumn(name = "idespacio")
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "idusuario_soli")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idusuario_recep")
    private Usuario vigilante;
}
