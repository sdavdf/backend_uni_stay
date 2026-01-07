package com.codesdfc.backend_uni_stay.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private int jwtExpirationInMs;

    private Key getSigningKey() {
        if (jwtSecret == null || jwtSecret.length() < 64) {
            throw new IllegalStateException("JWT secret debe tener al menos 64 caracteres para HS512");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generarToken(Authentication authentication) {
        String username = authentication.getName();
        Date fechaActual = new Date();
        Date fechaExpiracion = new Date(fechaActual.getTime() + jwtExpirationInMs);

        System.out.println("Generando token para: " + username);

        try {
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(new Date())
                    .setExpiration(fechaExpiracion)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                    .compact();

            System.out.println("Token generado exitosamente");
            System.out.println("Token: " + token);
            return token;

        } catch (Exception e) {
            System.err.println("Error generando token: " + e.getMessage());
            throw new RuntimeException("Error generando token JWT", e);
        }
    }

    public String obtenerNombreUsuarioDelJwt(String token) {
        try {
            System.out.println("Obteniendo usuario del token...");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            System.out.println("Usuario extraído: " + username);
            return username;

        } catch (Exception e) {
            System.err.println("Error obteniendo usuario del token: " + e.getMessage());
            throw new RuntimeException("Error extrayendo usuario del token JWT", e);
        }
    }

    public boolean validarToken(String token) {
        try {
            System.out.println("=== VALIDANDO TOKEN ===");
            System.out.println("Token recibido: " + (token != null ? token.substring(0, Math.min(50, token.length())) + "..." : "NULL"));
            System.out.println("Longitud del token: " + (token != null ? token.length() : 0));

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            System.out.println("Token VÁLIDO");
            return true;

        } catch (ExpiredJwtException e) {
            System.err.println("Token EXPIRADO: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token MAL FORMADO: " + e.getMessage());
            System.err.println("El token podría estar corrupto o tener formato incorrecto");
        } catch (SecurityException e) {
            System.err.println("Error de SEGURIDAD/FIRMA: " + e.getMessage());
            System.err.println("La firma del token no coincide - verifica la clave secreta");
        } catch (IllegalArgumentException e) {
            System.err.println("Token VACÍO o NULL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error general validando token: " + e.getClass().getSimpleName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}