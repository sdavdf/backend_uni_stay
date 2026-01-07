package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.model.Preferencia;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.service.PreferenciaService;
import com.codesdfc.backend_uni_stay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/preferencias")
@RequiredArgsConstructor
public class PreferenciaController {

    private final PreferenciaService preferenciaService;
    private final UsuarioService usuarioService;

    /**
     * CREAR mis preferencias (nuevo endpoint)
     */
    @PostMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> crearMisPreferencias(
            @RequestBody CrearPreferenciaRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // BUSCAR PREFERENCIAS EXISTENTES O CREAR NUEVAS (igual que InteresesController)
        Preferencia preferencia = preferenciaService.obtenerPorUsuario(usuario)
                .orElse(new Preferencia()); // Si no existe, creamos uno nuevo

        // Si es nuevo, establecer el usuario
        if (preferencia.getId() == null) {
            preferencia.setUsuario(usuario);
        }

        // Actualizar campos con los datos del request
        preferencia.setPreferenciaUbicacion(request.getPreferenciaUbicacion());
        preferencia.setPresupuestoMax(request.getPresupuestoMax());
        preferencia.setCompartirHabitacion(request.getCompartirHabitacion());
        preferencia.setFumador(request.getFumador());
        preferencia.setMascotasPermitidas(request.getMascotasPermitidas());
        preferencia.setHorasDormirPreferidas(request.getHorasDormirPreferidas());
        preferencia.setDistanciaMaxima(request.getDistanciaMaxima());
        preferencia.setTipoAlojamientoPreferido(request.getTipoAlojamientoPreferido());

        Preferencia guardada = preferenciaService.guardarPreferencias(preferencia);
        return ResponseEntity.ok(guardada);
    }

    //Obtener mis preferencias - CREA automáticamente si no existen

    @GetMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Preferencia> obtenerMisPreferencias(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Preferencia preferencia = preferenciaService.obtenerPorUsuario(usuario)
                .orElse(preferenciaService.crearPreferenciasPorDefecto(usuario));

        return ResponseEntity.ok(preferencia);
    }


    //Actualizar mis preferencias

    @PutMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Preferencia> actualizarMisPreferencias(
            @RequestBody Preferencia preferenciaRequest,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Preferencia preferencia = preferenciaService.obtenerPorUsuario(usuario)
                .orElse(preferenciaService.crearPreferenciasPorDefecto(usuario));

        // Actualizar campos
        if (preferenciaRequest.getPreferenciaUbicacion() != null)
            preferencia.setPreferenciaUbicacion(preferenciaRequest.getPreferenciaUbicacion());
        if (preferenciaRequest.getPresupuestoMax() != null)
            preferencia.setPresupuestoMax(preferenciaRequest.getPresupuestoMax());
        if (preferenciaRequest.getCompartirHabitacion() != null)
            preferencia.setCompartirHabitacion(preferenciaRequest.getCompartirHabitacion());
        if (preferenciaRequest.getFumador() != null)
            preferencia.setFumador(preferenciaRequest.getFumador());
        if (preferenciaRequest.getMascotasPermitidas() != null)
            preferencia.setMascotasPermitidas(preferenciaRequest.getMascotasPermitidas());
        if (preferenciaRequest.getHorasDormirPreferidas() != null)
            preferencia.setHorasDormirPreferidas(preferenciaRequest.getHorasDormirPreferidas());
        if (preferenciaRequest.getDistanciaMaxima() != null)
            preferencia.setDistanciaMaxima(preferenciaRequest.getDistanciaMaxima());
        if (preferenciaRequest.getTipoAlojamientoPreferido() != null)
            preferencia.setTipoAlojamientoPreferido(preferenciaRequest.getTipoAlojamientoPreferido());

        Preferencia actualizada = preferenciaService.guardarPreferencias(preferencia);
        return ResponseEntity.ok(actualizada);
    }

    //Obtener preferencias de un usuario (público)

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Preferencia> obtenerPreferenciasUsuario(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.obtenerEntidadPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Preferencia preferencia = preferenciaService.obtenerPorUsuario(usuario)
                .orElse(null);

        return ResponseEntity.ok(preferencia);
    }


    @DeleteMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> eliminarMisPreferencias(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Usar el nuevo método por usuario
        preferenciaService.eliminarPreferenciasPorUsuario(usuario);

        return ResponseEntity.ok(Map.of("mensaje", "Preferencias eliminadas correctamente"));
    }

    // Clase interna para el request de creación
    public static class CrearPreferenciaRequest {
        private String preferenciaUbicacion;
        private Double presupuestoMax;
        private Boolean compartirHabitacion;
        private Boolean fumador;
        private Boolean mascotasPermitidas;
        private String horasDormirPreferidas;
        private Double distanciaMaxima;
        private String tipoAlojamientoPreferido;

        // Getters y Setters
        public String getPreferenciaUbicacion() { return preferenciaUbicacion; }
        public void setPreferenciaUbicacion(String preferenciaUbicacion) { this.preferenciaUbicacion = preferenciaUbicacion; }
        public Double getPresupuestoMax() { return presupuestoMax; }
        public void setPresupuestoMax(Double presupuestoMax) { this.presupuestoMax = presupuestoMax; }
        public Boolean getCompartirHabitacion() { return compartirHabitacion; }
        public void setCompartirHabitacion(Boolean compartirHabitacion) { this.compartirHabitacion = compartirHabitacion; }
        public Boolean getFumador() { return fumador; }
        public void setFumador(Boolean fumador) { this.fumador = fumador; }
        public Boolean getMascotasPermitidas() { return mascotasPermitidas; }
        public void setMascotasPermitidas(Boolean mascotasPermitidas) { this.mascotasPermitidas = mascotasPermitidas; }
        public String getHorasDormirPreferidas() { return horasDormirPreferidas; }
        public void setHorasDormirPreferidas(String horasDormirPreferidas) { this.horasDormirPreferidas = horasDormirPreferidas; }
        public Double getDistanciaMaxima() { return distanciaMaxima; }
        public void setDistanciaMaxima(Double distanciaMaxima) { this.distanciaMaxima = distanciaMaxima; }
        public String getTipoAlojamientoPreferido() { return tipoAlojamientoPreferido; }
        public void setTipoAlojamientoPreferido(String tipoAlojamientoPreferido) { this.tipoAlojamientoPreferido = tipoAlojamientoPreferido; }
    }
}