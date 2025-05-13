package com.devicecontrolapi.config;

public class PublicRoutes { //Rutas accesibles sin autenticación JWT

    // Lista centralizada de rutas públicas
    public static final String[] PUBLIC_PATHS = {
        "/usuarios/login",      // Ruta pública para login
        "/usuarios/register",   // Ruta pública para registro
        "/password-reset/**" //Para el PasswordReset
    };
}
