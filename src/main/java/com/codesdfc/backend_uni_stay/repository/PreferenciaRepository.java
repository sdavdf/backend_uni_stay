package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.Preferencia;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenciaRepository extends JpaRepository<Preferencia, Long> {
    Optional<Preferencia> findByUsuario(Usuario usuario);
    Optional<Preferencia> findByUsuarioId(Long usuarioId);

    // Buscar usuarios con preferencias similares
    @Query("SELECT p FROM Preferencia p WHERE " +
            "p.presupuestoMax BETWEEN :presupuestoMin AND :presupuestoMax AND " +
            "p.compartirHabitacion = :compartirHabitacion AND " +
            "p.distanciaMaxima >= :distanciaMin")
    List<Preferencia> findWithSimilarPreferences(
            @Param("presupuestoMin") Double presupuestoMin,
            @Param("presupuestoMax") Double presupuestoMax,
            @Param("compartirHabitacion") Boolean compartirHabitacion,
            @Param("distanciaMin") Double distanciaMin);
}