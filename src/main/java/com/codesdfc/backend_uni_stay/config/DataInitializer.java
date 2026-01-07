package com.codesdfc.backend_uni_stay.config;

import com.codesdfc.backend_uni_stay.model.RolUsuario;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            Optional<Usuario> adminOpt = usuarioRepository.findByEmail("admin@unimatch.test");
            if (adminOpt.isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin Unimatch");
                admin.setEmail("admin@unimatch.test");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.getRoles().add(RolUsuario.ADMIN);
                usuarioRepository.save(admin);
                System.out.println("==> Usuario ADMIN creado: admin@unimatch.test / admin123");
            }
        };
    }
}