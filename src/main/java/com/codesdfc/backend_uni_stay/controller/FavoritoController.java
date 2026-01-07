package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.AlojamientoResumenDTO;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.service.FavoritoService;
import com.codesdfc.backend_uni_stay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favoritos")
@RequiredArgsConstructor
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final UsuarioService usuarioService;

    private Long currentUserId(Authentication authentication) {
        String email = authentication.getName();
        Usuario u = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return u.getId();
    }

    @GetMapping("/{alojamientoId}/es-favorito")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> esFavorito(
            @PathVariable Long alojamientoId,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        boolean esFav = favoritoService.esFavorito(userId, alojamientoId);
        return ResponseEntity.ok(Map.of("esFavorito", esFav));
    }

    @PostMapping("/{alojamientoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> add(
            @PathVariable Long alojamientoId,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        favoritoService.agregarFavorito(userId, alojamientoId);
        return ResponseEntity.ok(Map.of("mensaje", "Alojamiento guardado en favoritos"));
    }

    @DeleteMapping("/{alojamientoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> remove(
            @PathVariable Long alojamientoId,
            Authentication authentication
    ) {
        Long userId = currentUserId(authentication);
        favoritoService.quitarFavorito(userId, alojamientoId);
        return ResponseEntity.ok(Map.of("mensaje", "Alojamiento eliminado de favoritos"));
    }

    @GetMapping("/mis")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlojamientoResumenDTO>> misFavoritos(Authentication authentication) {
        Long userId = currentUserId(authentication);
        return ResponseEntity.ok(favoritoService.listarMisFavoritos(userId));
    }
}
