package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.RolUsuario;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    // Buscar usuarios que tengan un rol específico
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE r = :rol")
    List<Usuario> findByRol(@Param("rol") RolUsuario rol);

    // === NUEVOS MÉTODOS PARA FUNCIONALIDADES SOCIALES ===

    // Buscar usuarios por nombre (para búsqueda)
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Encontrar seguidores de un usuario (usuarios que siguen a este usuario)
    @Query("SELECT u FROM Usuario u JOIN u.siguiendo s WHERE s.id = :usuarioId")
    List<Usuario> findSeguidoresByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Verificar si un usuario sigue a otro
    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.siguiendo s WHERE u.id = :seguidorId AND s.id = :seguidoId")
    boolean existsBySeguidorIdAndSeguidoId(@Param("seguidorId") Long seguidorId, @Param("seguidoId") Long seguidoId);

    // Obtener usuarios más recientes (para recomendaciones)
    List<Usuario> findTop10ByOrderByFechaRegistroDesc();

    // Buscar usuarios excluyendo al actual (para recomendaciones)
    @Query("SELECT u FROM Usuario u WHERE u.id != :excludeId ORDER BY u.fechaRegistro DESC")
    List<Usuario> findRecomendadosExcludingUser(@Param("excludeId") Long excludeId, org.springframework.data.domain.Pageable pageable);
}