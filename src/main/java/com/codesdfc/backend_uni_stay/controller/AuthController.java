package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.LoginDTO;
import com.codesdfc.backend_uni_stay.dto.RegistroDTO;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.security.JwtTokenProvider;
import com.codesdfc.backend_uni_stay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar token JWT
        String token = jwtTokenProvider.generarToken(authentication);

        // Obtener usuario logueado
        Usuario usuario = usuarioService.obtenerPorEmail(loginDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // CREAR un objeto de respuesta personalizado que incluya perfilCompletado
        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", usuario.getId());
        usuarioResponse.put("nombre", usuario.getNombre());
        usuarioResponse.put("email", usuario.getEmail());
        usuarioResponse.put("telefono", usuario.getTelefono());
        usuarioResponse.put("perfilCompletado", usuario.getPerfilCompletado());
        usuarioResponse.put("roles", usuario.getRoles());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", usuarioResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registro(@RequestBody RegistroDTO registroDTO) {
        Usuario u = usuarioService.registrarUsuario(registroDTO);

        //  Devolver también perfilCompletado en el registro
        Map<String, Object> usuarioResponse = new HashMap<>();
        usuarioResponse.put("id", u.getId());
        usuarioResponse.put("nombre", u.getNombre());
        usuarioResponse.put("email", u.getEmail());
        usuarioResponse.put("telefono", u.getTelefono());
        usuarioResponse.put("perfilCompletado", u.getPerfilCompletado());
        usuarioResponse.put("roles", u.getRoles());



        return ResponseEntity.ok(usuarioResponse);
    }
}