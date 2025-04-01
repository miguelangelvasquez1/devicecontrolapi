package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "espacios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Espacio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idespacio;
    private String nombre;
    private String descripcion;
}