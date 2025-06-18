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
    private String url;
    private String serial;
    private String estado;
    private String observaciones; //Se añade despues de la version 1.0.0

    @ManyToOne
    @JoinColumn(name = "idespacio")
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "idtipoact")
    private TipoActivo tipoActivo;

    // interface Activo {
//   id: number;
//   codigo: string;
//   nombre: string;
//   tipo: string;
//   subtipo: string;
//   estado: 'Disponible' | 'Ocupado' | 'Mantenimiento';
//   ambienteAsignado?: string;
//   marca?: string;
//   modelo?: string;
//   codigoInterno?: string;
//   serial?: string;
//   observaciones?: string;
// }
}