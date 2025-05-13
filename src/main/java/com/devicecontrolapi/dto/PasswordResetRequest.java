package com.devicecontrolapi.dto;

import lombok.Data;

@Data
public class PasswordResetRequest { //Para setear la nueva contraseña luego de validar el código
    private String email;
    private String code;
    private String newPassword;
}