package com.devicecontrolapi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY_STRING = "mi-clave-secreta-super-segura-que-no-cambie";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));

    // Método para generar el JWT
    public String generateToken(String email, byte rol) {
        long expirationTime = 1000 * 60 * 60;  // 1 hora de expiración

        return Jwts.builder()
                .setSubject(email) //El subject será el email
                .claim("rol", rol)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SECRET_KEY)  // Firma con la clave generada
                .compact();
    }

    // Método para extraer el rol del JWT
    public byte extractRole(String token) {
        Claims claims = extractClaims(token);
        return (byte) claims.get("rol");
    }

    // Método para extraer los claims del JWT
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método para extraer el email del JWT
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Método para verificar si el token ha expirado
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Método para validar el JWT
    public boolean validateToken(String token, String email) {
        return (email.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}
