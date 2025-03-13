package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idactivo;
    private String nombre;
    private String codigoqr;
    private String serial;
    private char estado;

    @ManyToOne
    @JoinColumn(name = "idespacio")
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "idtipoact")
    private TipoActivo tipoActivo;
}