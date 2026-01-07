package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.SolicitudDetallesDTO;
import com.codesdfc.backend_uni_stay.model.Solicitud;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.service.SolicitudService;
import com.codesdfc.backend_uni_stay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;

    /**
     * Crear solicitud para un alojamiento
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Solicitud> crearSolicitud(
            @RequestBody CrearSolicitudRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario solicitante = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Solicitud solicitud = solicitudService.crearSolicitud(
                solicitante.getId(),
                request.getAlojamientoId(),
                request.getMensaje(),
                request.getOfertaPrecio()
        );

        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtener TODAS mis solicitudes enviadas (todos los estados)
     */
    @GetMapping("/enviadas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Solicitud>> obtenerMisSolicitudesEnviadas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesEnviadas(usuario.getId());
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtener TODAS mis solicitudes recibidas (todos los estados)
     */
    @GetMapping("/recibidas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesRecibidas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesRecibidas(usuario.getId());
        return ResponseEntity.ok(solicitudes);
    }

    // ===== Detalle por id =====
    @GetMapping("/{id}/detalles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SolicitudDetallesDTO> obtenerDetalle(@PathVariable Long id, Authentication auth) {
        // (opcional) validar que el usuario sea solicitante o receptor
        return ResponseEntity.ok(solicitudService.obtenerDetalleDTO(id));
    }

    /**
     * Obtener mis solicitudes enviadas por estado específico
     */
    @GetMapping("/enviadas/estado/{estado}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Solicitud>> obtenerMisSolicitudesEnviadasPorEstado(
            @PathVariable Solicitud.EstadoSolicitud estado,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesEnviadasPorEstado(usuario.getId(), estado);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtener mis solicitudes recibidas por estado específico
     */
    @GetMapping("/recibidas/estado/{estado}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Solicitud>> obtenerSolicitudesRecibidasPorEstado(
            @PathVariable Solicitud.EstadoSolicitud estado,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Solicitud> solicitudes = solicitudService.obtenerSolicitudesRecibidasPorEstado(usuario.getId(), estado);
        return ResponseEntity.ok(solicitudes);
    }

    /**
     * Obtener historial completo de solicitudes (con filtros opcionales)
     */
    @GetMapping("/historial")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> obtenerHistorialSolicitudes(
            @RequestParam(required = false) Solicitud.EstadoSolicitud estado,
            @RequestParam(required = false, defaultValue = "enviadas") String tipo,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Solicitud> solicitudes;

        if (estado != null) {
            if ("enviadas".equals(tipo)) {
                solicitudes = solicitudService.obtenerSolicitudesEnviadasPorEstado(usuario.getId(), estado);
            } else {
                solicitudes = solicitudService.obtenerSolicitudesRecibidasPorEstado(usuario.getId(), estado);
            }
        } else {
            if ("enviadas".equals(tipo)) {
                solicitudes = solicitudService.obtenerSolicitudesEnviadas(usuario.getId());
            } else {
                solicitudes = solicitudService.obtenerSolicitudesRecibidas(usuario.getId());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("solicitudes", solicitudes);
        response.put("total", solicitudes.size());
        response.put("tipo", tipo);
        response.put("estado", estado);

        return ResponseEntity.ok(response);
    }

    /**
     * Responder a una solicitud (aceptar/rechazar)
     */
    @PutMapping("/{solicitudId}/responder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Solicitud> responderSolicitud(
            @PathVariable Long solicitudId,
            @RequestBody ResponderSolicitudRequest request,
            Authentication authentication) {

        // Verificar que el usuario es el receptor de la solicitud
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Solicitud solicitud = solicitudService.responderSolicitud(
                solicitudId,
                request.getEstado(),
                request.getMotivo()
        );

        return ResponseEntity.ok(solicitud);
    }

    /**
     * Cancelar mi solicitud
     */
    @PutMapping("/{solicitudId}/cancelar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Solicitud> cancelarSolicitud(
            @PathVariable Long solicitudId,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Solicitud solicitud = solicitudService.responderSolicitud(
                solicitudId,
                Solicitud.EstadoSolicitud.CANCELADA,
                "Cancelada por el solicitante"
        );

        return ResponseEntity.ok(solicitud);
    }

    /**
     * Obtener estadísticas de solicitudes
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> estadisticas = Map.of(
                "pendientesRecibidas", solicitudService.contarSolicitudesPendientes(usuario.getId()),
                "aceptadasEnviadas", solicitudService.contarSolicitudesAceptadas(usuario.getId())
        );

        return ResponseEntity.ok(estadisticas);
    }

    // Clases internas para requests
    public static class CrearSolicitudRequest {
        private Long alojamientoId;
        private String mensaje;
        private Double ofertaPrecio;

        public Long getAlojamientoId() { return alojamientoId; }
        public void setAlojamientoId(Long alojamientoId) { this.alojamientoId = alojamientoId; }
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        public Double getOfertaPrecio() { return ofertaPrecio; }
        public void setOfertaPrecio(Double ofertaPrecio) { this.ofertaPrecio = ofertaPrecio; }
    }

    public static class ResponderSolicitudRequest {
        private Solicitud.EstadoSolicitud estado;
        private String motivo;

        public Solicitud.EstadoSolicitud getEstado() { return estado; }
        public void setEstado(Solicitud.EstadoSolicitud estado) { this.estado = estado; }
        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }
}