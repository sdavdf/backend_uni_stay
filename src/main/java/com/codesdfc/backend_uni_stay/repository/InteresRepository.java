package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.Interes;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InteresRepository extends JpaRepository<Interes, Long> {
    Optional<Interes> findByUsuario(Usuario usuario);
    Optional<Interes> findByUsuarioId(Long usuarioId);

    @Query("SELECT i FROM Interes i WHERE " +
            "i.personalidad = :personalidad AND " +
            "i.nivelLimpieza = :nivelLimpieza AND " +
            "i.horarioEstudio = :horarioEstudio")
    List<Interes> findWithSimilarInterests(
            @Param("personalidad") String personalidad,
            @Param("nivelLimpieza") String nivelLimpieza,
            @Param("horarioEstudio") String horarioEstudio);

    // 👇 Firma flexible: Collection
    @Query("SELECT i FROM Interes i JOIN i.hobbies h WHERE h IN :hobbies")
    List<Interes> findByHobbiesIn(@Param("hobbies") Collection<String> hobbies);
}
