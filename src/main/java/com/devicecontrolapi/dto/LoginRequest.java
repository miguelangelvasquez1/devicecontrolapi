package com.devicecontrolapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {  //Capturar errores de token inválido

    private String email;
    private String clave;
}
