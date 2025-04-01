package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "mov_activos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovActivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idmov;
    private Date fecha;  //Poner fechaDevol?
    private String descrimov;
    private char estado;

    @ManyToOne
    @JoinColumn(name = "idactivo")
    private Activo activo;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idespacio_ori")
    private Espacio espacioOrigen;

    @ManyToOne
    @JoinColumn(name = "idespacio_des")
    private Espacio espacioDestino;
}