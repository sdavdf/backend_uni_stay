package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.InteresDTO;
import com.codesdfc.backend_uni_stay.model.Interes;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import com.codesdfc.backend_uni_stay.service.InteresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/intereses")
@RequiredArgsConstructor
public class InteresController {

    private final UsuarioRepository usuarioRepository;
    private final InteresService interesService;

    // GET: obtener mis intereses (solo lo que está en BD)
    @GetMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresDTO> obtenerMisIntereses(Authentication auth) {
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return interesService.obtenerPorUsuario(usuario)
                .map(interes -> ResponseEntity.ok(interesService.mapToDTO(interes)))
                .orElse(ResponseEntity.ok(null)); // si no tiene intereses aún → null
    }

    // PUT: crear o actualizar mis intereses
    @PutMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresDTO> actualizarMisIntereses(
            @RequestBody InteresDTO dto,
            Authentication auth
    ) {
        String email = auth.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Interes guardado = interesService.guardarOActualizarIntereses(usuario, dto);
        InteresDTO resp = interesService.mapToDTO(guardado);

        return ResponseEntity.ok(resp);
    }
}
