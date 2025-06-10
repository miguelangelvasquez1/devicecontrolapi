package com.devicecontrolapi.util;

import com.devicecontrolapi.config.PublicRoutes;
import com.devicecontrolapi.exceptions.InvalidTokenException;
import com.devicecontrolapi.exceptions.TokenMissingException;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Verifica si la ruta está en las rutas públicas
        String path = request.getServletPath();
        for (String route : PublicRoutes.PUBLIC_PATHS) {
            if (path.matches(route.replace("**", ".*"))) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            // Buscar primero en el header Authorization
            String jwtToken = null;
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwtToken = authHeader.substring(7);
            }

            // Si no está en el header, buscarlo como parámetro de URL
            if (jwtToken == null) {
                jwtToken = request.getParameter("token");
            }

            // Si sigue sin token, lanzar excepción personalizada
            if (jwtToken == null || jwtToken.isBlank()) {
                throw new TokenMissingException(); // Token no presente
            }

            final String emailSubject = jwtUtil.extractUsername(jwtToken);

            if (emailSubject == null || !jwtUtil.validateToken(jwtToken, emailSubject)) {
                throw new InvalidTokenException(); // Token inválido
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    emailSubject, null, Collections.emptyList());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e);
            authenticationEntryPoint.commence(request, response,
                    new org.springframework.security.core.AuthenticationException(
                            "Token expirado, haga la renovación eche", e) {
                    });
        } catch (TokenMissingException | InvalidTokenException e) {
            SecurityContextHolder.clearContext();
            request.setAttribute("exception", e);
            authenticationEntryPoint.commence(request, response,
                    new org.springframework.security.core.AuthenticationException(e.getMessage(), e) {
                    });
        }
    }
}
