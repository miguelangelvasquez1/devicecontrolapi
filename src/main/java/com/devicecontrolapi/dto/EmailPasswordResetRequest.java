package com.devicecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailPasswordResetRequest { //Para enviar el email de recuperación de contraseña

    private String email;
    private String name;

    public EmailPasswordResetRequest() {
        
    }
}
