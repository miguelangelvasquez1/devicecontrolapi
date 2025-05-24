package com.devicecontrolapi.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String nombre;
    private String email;
    private String telefono;
    private Byte rol;
}
