package com.devicecontrolapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idusuario;
    private String nombre;
    private String email;
    private String clave;
    private String telefono;
    private Byte rol;

    public String rolNumberToString() {
        switch (rol) {
            case 1:
                return "Vigilante";
            case 2:
                return "Instructor";
            case 3:
                return "Almacén";
            default:
                return "Desconocido";
        }
    }
}