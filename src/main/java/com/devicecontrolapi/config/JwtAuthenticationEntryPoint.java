package com.devicecontrolapi.config;

import com.devicecontrolapi.exceptions.InvalidTokenException;
import com.devicecontrolapi.exceptions.TokenMissingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint { //Para manejar las excepciones de token JWT

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Throwable realCause = (Throwable) request.getAttribute("exception");

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (realCause instanceof TokenMissingException) {
            response.getWriter().write("{\"error\": \"Token faltante o no proporcionado\"}");
        } else if (realCause instanceof InvalidTokenException) {
            response.getWriter().write("{\"error\": \"Token inválido o expirado\"}");
        } else {
            response.getWriter().write("{\"error\": \"No autorizado\"}");
        }
    }
}
