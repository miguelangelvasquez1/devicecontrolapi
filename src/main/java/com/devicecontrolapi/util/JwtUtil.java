package com.devicecontrolapi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    // Clave secreta para firmar el JWT (puedes moverla a tu archivo de configuración)
    private SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Método para generar el JWT
    public String generateToken(String email, byte rol) {
        long expirationTime = 1000 * 60 * 60;  // 1 hora de expiración

        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)  // Firma con la clave generada
                .compact();
    }

    // Método para extraer el rol del JWT
    public byte extractRole(String token) {
        Claims claims = extractClaims(token);
        return (byte) claims.get("rol");
    }

    // Método para extraer los claims del JWT
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // Clave secreta
                .parseClaimsJws(token) // Parsear el JWT
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
