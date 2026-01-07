package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.Favorito;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    boolean existsByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    Optional<Favorito> findByUsuarioIdAndAlojamientoId(Long usuarioId, Long alojamientoId);

    List<Favorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);

    List<Favorito> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
}
