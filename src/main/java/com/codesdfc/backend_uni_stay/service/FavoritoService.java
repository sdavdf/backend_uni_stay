package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.AlojamientoResumenDTO;
import com.codesdfc.backend_uni_stay.model.Alojamiento;
import com.codesdfc.backend_uni_stay.model.Favorito;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.AlojamientoRepository;
import com.codesdfc.backend_uni_stay.repository.FavoritoRepository;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlojamientoRepository alojamientoRepository;

    @Transactional(readOnly = true)
    public boolean esFavorito(Long usuarioId, Long alojamientoId) {
        return favoritoRepository.existsByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId);
    }

    @Transactional
    public void agregarFavorito(Long usuarioId, Long alojamientoId) {
        if (favoritoRepository.existsByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId)) {
            // idempotente
            return;
        }
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Alojamiento a = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        Favorito fav = Favorito.builder()
                .usuario(u)
                .alojamiento(a)
                .build();
        favoritoRepository.save(fav);
    }

    @Transactional
    public void quitarFavorito(Long usuarioId, Long alojamientoId) {
        favoritoRepository.findByUsuarioIdAndAlojamientoId(usuarioId, alojamientoId)
                .ifPresent(favoritoRepository::delete);
    }

    @Transactional(readOnly = true)
    public List<AlojamientoResumenDTO> listarMisFavoritos(Long usuarioId) {
        return favoritoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId).stream()
                .map(Favorito::getAlojamiento)
                .map(AlojamientoResumenDTO::from)
                .toList();
    }
}
