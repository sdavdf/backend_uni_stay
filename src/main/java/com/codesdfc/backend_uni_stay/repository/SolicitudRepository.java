package com.codesdfc.backend_uni_stay.repository;

import com.codesdfc.backend_uni_stay.model.Alojamiento;
import com.codesdfc.backend_uni_stay.model.Solicitud;
import com.codesdfc.backend_uni_stay.model.Solicitud.EstadoSolicitud;
import com.codesdfc.backend_uni_stay.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    // Buscar TODAS las solicitudes enviadas por un usuario
    List<Solicitud> findBySolicitante(Usuario solicitante);

    // Buscar TODAS las solicitudes recibidas por un usuario
    List<Solicitud> findByReceptor(Usuario receptor);
    // Solicitudes enviadas por un usuario
    List<Solicitud> findBySolicitanteAndEstado(Usuario solicitante, EstadoSolicitud estado);

    // Solicitudes recibidas por un usuario (para sus alojamientos)
    List<Solicitud> findByReceptorAndEstado(Usuario receptor, EstadoSolicitud estado);

    // Solicitudes para un alojamiento específico
    List<Solicitud> findByAlojamientoAndEstado(Alojamiento alojamiento, EstadoSolicitud estado);

    // Verificar si ya existe una solicitud pendiente
    @Query("SELECT COUNT(s) > 0 FROM Solicitud s WHERE " +
            "s.solicitante.id = :solicitanteId AND " +
            "s.alojamiento.id = :alojamientoId AND " +
            "s.estado = 'PENDIENTE'")
    boolean existsSolicitudPendiente(@Param("solicitanteId") Long solicitanteId,
                                     @Param("alojamientoId") Long alojamientoId);

    // Estadísticas de solicitudes
    @Query("SELECT COUNT(s) FROM Solicitud s WHERE s.receptor.id = :usuarioId AND s.estado = 'PENDIENTE'")
    Long countSolicitudesPendientes(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(s) FROM Solicitud s WHERE s.solicitante.id = :usuarioId AND s.estado = 'ACEPTADA'")
    Long countSolicitudesAceptadas(@Param("usuarioId") Long usuarioId);

    @EntityGraph(attributePaths = {"alojamiento", "alojamiento.publicador", "solicitante"})
    Optional<Solicitud> findOneWithGraphById(Long id);
}