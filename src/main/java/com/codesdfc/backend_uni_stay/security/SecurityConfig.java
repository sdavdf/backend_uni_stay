package com.codesdfc.backend_uni_stay.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Se activa CORS
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/api/auth/**",
                                "/public/**",

                                // Alojamientos públicos
                                "/alojamientos",
                                "/alojamientos/{id:[0-9]+}",
                                "/alojamientos/buscar",
                                "/alojamientos/filtros",
                                "/alojamientos/ordenar/**",
                                "/alojamientos/filtros/**",

                                "/api/favoritos/**",

                                "/api/usuarios/mi-perfil/perfil-completado",


                                // Usuarios públicos
                                "/usuarios/{id:[0-9]+}/perfil",
                                "/usuarios/{id:[0-9]+}/seguidores",
                                "/usuarios/{id:[0-9]+}/siguiendo",
                                "/usuarios/{id:[0-9]+}/seguir",
                                "/api/usuarios/buscar",
                                "/api/usuarios/rol/**",

                                "/fotos",
                                "/uploads/**",

                                "/mi-perfil/completo"
                        ).permitAll()

                        // ENDPOINTS PRIVADOS (requieren autenticación)
                        .requestMatchers(
                                // Usuarios privados
                                "/api/usuarios/mi-perfil",
                                "/api/usuarios/mi-perfil/**",
                                "/usuarios/{id:[0-9]+}/seguir",
                                "/usuarios/{id:[0-9]+}/sigo",
                                "/api/usuarios/recomendados",

                                // Preferencias e intereses
                                "/api/preferencias/**",
                                "/api/intereses/**",
                                "/api/intereses/usuario/mi-perfil/**",

                                "/api/favoritos/**",
                                "/fotos",
                                "/api/mi-perfil/completo",

                                // Alojamientos privados
                                "/alojamientos/mis-publicaciones",
                                "/alojamientos",
                                "/alojamientos/{id:[0-9]+}",

                                // Solicitudes
                                "/api/solicitudes/**",

                                // Recomendaciones
                                "/api/recomendaciones/**",
                                "/api/uploads/**"
                        ).authenticated()

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // Configuración CORS actualizada
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080","http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Los demás beans permanecen igual
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}