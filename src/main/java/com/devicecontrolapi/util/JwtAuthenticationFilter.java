package com.devicecontrolapi.util;

import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//Verificacion del token en cada solicitud
@WebFilter("/activos/*") // Aplica a las rutas que comienzan con "/user/"
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Eliminar "Bearer " del encabezado

            try {
                String email = jwtUtil.extractUsername(token);
                if (jwtUtil.validateToken(token, email)) {
                    System.out.println("hola");
                    logger.info("Hola----------------------------------------------");
                    // El token es válido, se puede continuar con la solicitud
                    // Puedes establecer la autenticación en el contexto de seguridad aquí si es necesario
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Token inválido
                    response.getWriter().write("Token no válido o expirado");
                    return;
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // Token inválido
                response.getWriter().write("Token no válido o expirado");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
