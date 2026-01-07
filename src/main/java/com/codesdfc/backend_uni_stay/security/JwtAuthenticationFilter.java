package com.codesdfc.backend_uni_stay.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = obtenerJwtDeLaSolicitud(request);

        // DEBUG DETALLADO
        System.out.println("\n=== FILTRO JWT ===");
        System.out.println("URL: " + request.getRequestURI());
        System.out.println("Método: " + request.getMethod());
        System.out.println("Token presente: " + (token != null ? "SÍ" : "NO"));

        if (StringUtils.hasText(token)) {
            boolean esValido = jwtTokenProvider.validarToken(token);
            System.out.println("Token válido: " + esValido);

            if (esValido && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    String username = jwtTokenProvider.obtenerNombreUsuarioDelJwt(token);
                    System.out.println("Usuario autenticado: " + username);

                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    System.out.println("Autenticación exitosa para: " + username);

                } catch (Exception e) {
                    logger.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage());
                    System.err.println("Error en autenticación: " + e.getMessage());
                }
            }
        } else {
            System.out.println(" No se encontró token en el header Authorization");
        }

        filterChain.doFilter(request, response);
    }

    private String obtenerJwtDeLaSolicitud(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
