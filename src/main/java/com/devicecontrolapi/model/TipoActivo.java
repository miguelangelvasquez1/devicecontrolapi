package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_activos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoActivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtipoact;
    private String nombre;
}
