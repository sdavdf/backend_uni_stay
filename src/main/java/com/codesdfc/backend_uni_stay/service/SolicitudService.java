package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.SolicitudDetallesDTO;
import com.codesdfc.backend_uni_stay.model.*;
import com.codesdfc.backend_uni_stay.repository.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudService {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioService usuarioService;
    private final AlojamientoService alojamientoService;

    @Transactional
    public Solicitud crearSolicitud(Long solicitanteId, Long alojamientoId, String mensaje, Double ofertaPrecio) {
        Usuario solicitante = usuarioService.obtenerEntidadPorId(solicitanteId)
                .orElseThrow(() -> new RuntimeException("Solicitante no encontrado"));

        Alojamiento alojamiento = alojamientoService.obtenerEntityPorId(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));

        // Verificar que no existe solicitud pendiente
        if (solicitudRepository.existsSolicitudPendiente(solicitanteId, alojamientoId)) {
            throw new RuntimeException("Ya tienes una solicitud pendiente para este alojamiento");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setSolicitante(solicitante);
        solicitud.setReceptor(alojamiento.getPublicador());
        solicitud.setAlojamiento(alojamiento);
        solicitud.setMensaje(mensaje);
        solicitud.setOfertaPrecio(ofertaPrecio);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado(Solicitud.EstadoSolicitud.PENDIENTE);

        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public Solicitud responderSolicitud(Long solicitudId, Solicitud.EstadoSolicitud estado, String motivo) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        solicitud.setEstado(estado);
        solicitud.setFechaRespuesta(LocalDateTime.now());
        solicitud.setMotivoRechazo(motivo);

        return solicitudRepository.save(solicitud);
    }

    public List<Solicitud> obtenerSolicitudesEnviadas(Long usuarioId) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return solicitudRepository.findBySolicitante(usuario);
    }

    public List<Solicitud> obtenerSolicitudesRecibidas(Long usuarioId) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return solicitudRepository.findByReceptor(usuario);
    }

    /**
     * Obtener solicitudes por estado específico
     */
    public List<Solicitud> obtenerSolicitudesEnviadasPorEstado(Long usuarioId, Solicitud.EstadoSolicitud estado) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return solicitudRepository.findBySolicitanteAndEstado(usuario, estado);
    }

    /**
     * Obtener solicitudes recibidas por estado específico
     */
    public List<Solicitud> obtenerSolicitudesRecibidasPorEstado(Long usuarioId, Solicitud.EstadoSolicitud estado) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return solicitudRepository.findByReceptorAndEstado(usuario, estado);
    }


    public List<Solicitud> obtenerSolicitudesPorAlojamiento(Long alojamientoId) {
        Alojamiento alojamiento = alojamientoService.obtenerEntityPorId(alojamientoId)
                .orElseThrow(() -> new RuntimeException("Alojamiento no encontrado"));
        return solicitudRepository.findByAlojamientoAndEstado(alojamiento, Solicitud.EstadoSolicitud.PENDIENTE);
    }

    public Long contarSolicitudesPendientes(Long usuarioId) {
        return solicitudRepository.countSolicitudesPendientes(usuarioId);
    }

    public Long contarSolicitudesAceptadas(Long usuarioId) {
        return solicitudRepository.countSolicitudesAceptadas(usuarioId);
    }

    public SolicitudDetallesDTO obtenerDetalleDTO(Long solicitudId) {
        Solicitud s = solicitudRepository.findOneWithGraphById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        return toDTO(s);
    }

    private SolicitudDetallesDTO toDTO(Solicitud s) {
        SolicitudDetallesDTO dto = new SolicitudDetallesDTO();
        dto.id = s.getId();
        dto.mensaje = s.getMensaje();
        dto.fechaSolicitud = s.getFechaSolicitud();
        dto.fechaRespuesta = s.getFechaRespuesta();
        dto.estado = s.getEstado().name();
        dto.motivoRechazo = s.getMotivoRechazo();
        dto.ofertaPrecio = s.getOfertaPrecio();

        Usuario solicitante = s.getSolicitante();
        if (solicitante != null) {
            dto.solicitanteId = solicitante.getId();
            dto.solicitanteNombre = solicitante.getNombre();
            dto.solicitanteEmail = solicitante.getEmail();
            dto.solicitanteTelefono = solicitante.getTelefono();
        }

        Alojamiento a = s.getAlojamiento();
        if (a != null) {
            dto.alojamientoId = a.getId();
            dto.alojamientoNombre = a.getNombre();
            Usuario pub = a.getPublicador();
            if (pub != null) {
                dto.publicadorId = pub.getId();
                dto.publicadorNombre = pub.getNombre();
                dto.publicadorEmail = pub.getEmail();
                dto.publicadorTelefono = pub.getTelefono();
            }
        }
        return dto;
    }

}