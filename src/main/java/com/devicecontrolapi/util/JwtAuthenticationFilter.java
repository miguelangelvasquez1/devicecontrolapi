package com.devicecontrolapi.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getServletPath().contains("/login") || request.getServletPath().contains("register")) { //Poner register
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        final String emailSubject = jwtUtil.extractUsername(jwtToken);
        if (emailSubject == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }
        final var authToken = new UsernamePasswordAuthenticationToken(
                emailSubject,
                null,
                Collections.emptyList()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = authHeader.substring(7);
//        try {
//            //Este será el token decodificado
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY.getBytes())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            Integer rol = claims.get("rol", Integer.class);
//
//            List<GrantedAuthority> authorities = new ArrayList<>();
//
//            if (rol != null) {
//                if (rol == 1) {  // ejemplo: 1 es admin
//                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//                } else if (rol == 2) {  // ejemplo: 2 es usuario normal
//                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//                }
//            }
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
//            authentication.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request)
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
}


//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.logging.Logger;
//
////Verificacion del token en cada solicitud
//@WebFilter("/activos/*") // Aplica a las rutas que comienzan con "/user/"
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private static final Logger logger = Logger.getLogger(JwtAuthenticationFilter.class.getName());
//
//    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        String token = request.getHeader("Authorization");
//
//        if (token != null && token.startsWith("Bearer ")) {
//            token = token.substring(7); // Eliminar "Bearer " del encabezado
//
//            try {
//                String email = jwtUtil.extractUsername(token);
//                if (jwtUtil.validateToken(token, email)) {
//                    // Establece autenticación en el contexto de seguridad
//                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                            email, null, Collections.emptyList());
//
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                } else {
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.getWriter().write("Token no válido o expirado");
//                    return;
//                }
//            } catch (Exception e) {
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Token no válido o expirado");
//                return;
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//}
