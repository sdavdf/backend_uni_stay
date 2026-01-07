package com.codesdfc.backend_uni_stay.controller;

import com.codesdfc.backend_uni_stay.dto.PerfilCompletoDTO;
import com.codesdfc.backend_uni_stay.dto.PerfilEstadoDTO;
import com.codesdfc.backend_uni_stay.dto.UsuarioDTO;
import com.codesdfc.backend_uni_stay.dto.UsuarioPerfilDTO;
import com.codesdfc.backend_uni_stay.model.RolUsuario;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;


    /**
     * Obtener perfil público completo de cualquier usuario
     */
    @GetMapping("/{id}/perfil")
    public ResponseEntity<UsuarioPerfilDTO> obtenerPerfilPublico(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPerfilCompletoPorId(id));
    }

    /**
     * Buscar usuarios por nombre (retorna perfiles completos)
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioPerfilDTO>> buscarUsuarios(@RequestParam String nombre) {
        return ResponseEntity.ok(usuarioService.buscarUsuariosPerfilPorNombre(nombre));
    }

    /**
     * Obtener usuarios por rol (perfiles completos)
     */
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioPerfilDTO>> obtenerPorRol(@PathVariable RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.obtenerPerfilesPorRol(rol));
    }

    /**
     * Obtener seguidores de un usuario (perfiles completos)
     */
    @GetMapping("/{id}/seguidores")
    public ResponseEntity<List<UsuarioPerfilDTO>> obtenerSeguidores(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerSeguidoresPerfil(id));
    }

    /**
     * Obtener usuarios que sigue un usuario (perfiles completos)
     */
    @GetMapping("/{id}/siguiendo")
    public ResponseEntity<List<UsuarioPerfilDTO>> obtenerSiguiendo(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerSiguiendoPerfil(id));
    }

    // === ENDPOINTS PRIVADOS  ===

    /**
     * Obtener MI perfil completo
     */
    @GetMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioPerfilDTO> obtenerMiPerfil(Authentication authentication) {
        String email = authentication.getName();
        UsuarioPerfilDTO perfil = usuarioService.obtenerPerfilCompletoPorEmail(email);
        return ResponseEntity.ok(perfil);
    }

    @PatchMapping("/mi-perfil/perfil-completado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> marcarPerfilCompletado(
            @RequestBody(required = false) PerfilCompletadoRequest req,
            Authentication authentication
    ) {
        try {
            System.out.println("PATCH /perfil-completado - Request recibida");
            System.out.println("Headers - Auth: " + authentication.getName());
            System.out.println("Body recibido: " + req);

            if (req == null || req.getCompletado() == null) {
                System.out.println("Body inválido o vacío");
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Campo 'completado' es requerido",
                        "ejemplo", "{\"completado\": true}"
                ));
            }

            boolean completado = req.getCompletado();
            System.out.println("Marcando perfil como completado: " + completado + " para usuario: " + authentication.getName());

            // Ejecutar el servicio
            usuarioService.marcarPerfilCompletado(authentication.getName(), completado);

            System.out.println("Perfil actualizado exitosamente en BD");

            return ResponseEntity.ok(Map.of(
                    "message", "Perfil actualizado exitosamente",
                    "completado", completado,
                    "usuario", authentication.getName()
            ));

        } catch (Exception e) {
            System.err.println("Error en marcarPerfilCompletado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error actualizando perfil",
                    "detalle", e.getMessage()
            ));
        }
    }

    // UsuarioController.java
    @GetMapping("/mi-perfil/estado")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilEstadoDTO> obtenerEstadoPerfil(Authentication authentication) {
        String email = authentication.getName();
        boolean completado = usuarioService.obtenerPerfilCompletadoPorEmail(email);
        return ResponseEntity.ok(new PerfilEstadoDTO(completado));
    }



    /**
     * Actualizar MI perfil
     */
    @PutMapping("/mi-perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioPerfilDTO> actualizarMiPerfil(
            @RequestBody ActualizarPerfilRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos
        if (request.getNombre() != null) usuario.setNombre(request.getNombre());
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());
        if (request.getGenero() != null) usuario.setGenero(request.getGenero());
        if (request.getEdad() != null) usuario.setEdad(request.getEdad());
        if (request.getCarrera() != null) usuario.setCarrera(request.getCarrera());
        if (request.getSemestre() != null) usuario.setSemestre(request.getSemestre());
        if (request.getBio() != null) usuario.setBio(request.getBio());

        Usuario actualizado = usuarioService.actualizarUsuario(usuario);
        UsuarioPerfilDTO perfilActualizado = usuarioService.obtenerPerfilCompletoPorId(actualizado.getId());

        return ResponseEntity.ok(perfilActualizado);
    }

    // === FUNCIONALIDADES SOCIALES ===

    /**
     * Seguir a un usuario
     */
    @PostMapping("/{id}/seguir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> seguirUsuario(
            @PathVariable Long id,
            Authentication authentication) {

        String emailSeguidor = authentication.getName();
        Usuario seguidor = usuarioService.obtenerPorEmail(emailSeguidor)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioService.seguirUsuario(seguidor.getId(), id);

        return ResponseEntity.ok(Map.of("mensaje", "Ahora sigues a este usuario"));
    }

    /**
     * Dejar de seguir a un usuario
     */
    @DeleteMapping("/{id}/seguir")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> dejarDeSeguirUsuario(
            @PathVariable Long id,
            Authentication authentication) {

        String emailSeguidor = authentication.getName();
        Usuario seguidor = usuarioService.obtenerPorEmail(emailSeguidor)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioService.dejarDeSeguirUsuario(seguidor.getId(), id);

        return ResponseEntity.ok(Map.of("mensaje", "Has dejado de seguir a este usuario"));
    }

    /**
     * Verificar si sigo a un usuario
     */
    @GetMapping("/{id}/sigo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> verificarSiSigo(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean sigo = usuarioService.verificarSiSigo(usuario.getId(), id);

        return ResponseEntity.ok(Map.of("sigo", sigo));
    }

    /**
     * Obtener usuarios recomendados para seguir (perfiles completos)
     */
    @GetMapping("/recomendados")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UsuarioPerfilDTO>> obtenerRecomendados(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<UsuarioPerfilDTO> recomendados = usuarioService.obtenerUsuariosRecomendadosPerfil(usuario.getId());
        return ResponseEntity.ok(recomendados);
    }

    // === ESTADÍSTICAS ===

    /**
     * Obtener estadísticas de mi perfil
     */
    @GetMapping("/mi-perfil/estadisticas")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> obtenerMisEstadisticas(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.obtenerPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> estadisticas = usuarioService.obtenerEstadisticasUsuario(usuario.getId());
        return ResponseEntity.ok(estadisticas);
    }

    // === 🔧 ADMIN ENDPOINTS (UsuarioDTO básico) ===

    /**
     * Obtener todos los usuarios (lista básica - admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodosLosUsuarios());
    }

    /**
     * Asignar rol a usuario (desde frontend modo selección)
     */
    @PostMapping("/{id}/rol")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioPerfilDTO> asignarRol(
            @PathVariable Long id,
            @RequestBody AsignarRolRequest request,
            Authentication auth) {

        System.out.println("POST /usuarios/" + id + "/rol llamado");
        System.out.println("Auth: " + auth.getName());
        System.out.println("Rol recibido en body: " + request.getRol());

        Usuario usuario = usuarioService.asignarRol(id, request.getRol());
        UsuarioPerfilDTO perfil = usuarioService.obtenerPerfilCompletoPorId(usuario.getId());

        System.out.println("[POST /rol] JSON recibido: " + request.getRol());

        return ResponseEntity.ok(perfil);
    }


    @GetMapping("/mi-perfil/completo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilCompletoDTO> obtenerMiPerfilCompleto(Authentication authentication) {
        String emailUsuario = authentication.getName();
        PerfilCompletoDTO perfil = usuarioService.obtenerMiPerfilCompleto(emailUsuario);
        return ResponseEntity.ok(perfil);
    }


    // === CLASES INTERNAS PARA REQUESTS ===

    public static class ActualizarPerfilRequest {
        private String nombre;
        private String telefono;
        private String fotoPerfil;
        private String genero;
        private Integer edad;
        private String carrera;
        private Integer semestre;
        private String bio;

        // getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getFotoPerfil() { return fotoPerfil; }
        public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
        public String getGenero() { return genero; }
        public void setGenero(String genero) { this.genero = genero; }
        public Integer getEdad() { return edad; }
        public void setEdad(Integer edad) { this.edad = edad; }
        public String getCarrera() { return carrera; }
        public void setCarrera(String carrera) { this.carrera = carrera; }
        public Integer getSemestre() { return semestre; }
        public void setSemestre(Integer semestre) { this.semestre = semestre; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
    }

    public static class AsignarRolRequest {
        private RolUsuario rol;

        public RolUsuario getRol() { return rol; }
        public void setRol(RolUsuario rol) { this.rol = rol; }
    }

    // Clase request mejorada
    public static class PerfilCompletadoRequest {
        private Boolean completado;

        // Para compatibilidad con diferentes nombres
        private Boolean perfilCompletado;

        public Boolean getCompletado() {
            return completado != null ? completado : perfilCompletado;
        }

        public void setCompletado(Boolean completado) {
            this.completado = completado;
        }

        public Boolean getPerfilCompletado() {
            return perfilCompletado;
        }

        public void setPerfilCompletado(Boolean perfilCompletado) {
            this.perfilCompletado = perfilCompletado;
        }
    }
}