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
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new TokenMissingException(); // Si no hay token o esta mal escrito, etc
            }

            final String jwtToken = authHeader.substring(7);
            final String emailSubject = jwtUtil.extractUsername(jwtToken);

            if (emailSubject == null || !jwtUtil.validateToken(jwtToken, emailSubject)) {
                throw new InvalidTokenException(); // Si el token no es válido
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
                    new org.springframework.security.core.AuthenticationException("Token expirado, haga la renovación eche", e) {
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
