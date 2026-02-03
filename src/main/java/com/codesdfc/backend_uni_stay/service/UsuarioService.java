package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.*;
import com.codesdfc.backend_uni_stay.model.RolUsuario;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.InteresRepository;
import com.codesdfc.backend_uni_stay.repository.PreferenciaRepository;
import com.codesdfc.backend_uni_stay.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private final PreferenciaRepository preferenciaRepository;
    private final InteresRepository interesRepository;

    @Transactional
    public Usuario registrarUsuario(RegistroDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email en uso");
        }

        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setEmail(dto.getEmail());
        u.setTelefono(dto.getTelefono());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setFechaRegistro(LocalDateTime.now());

        u.setPerfilCompletado(false);

        System.out.println("Nuevo usuario - perfilCompletado: " + u.getPerfilCompletado());



        // Asignar rol por defecto
        //u.getRoles().add(RolUsuario.BUSCADOR);

        Usuario guardado = usuarioRepository.save(u);
        System.out.println("Usuario guardado - perfilCompletado: " + guardado.getPerfilCompletado());

        return guardado;    }

    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::convertirAUsuarioDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return convertirAUsuarioDTO(usuario);
    }

    // === NUEVOS MÉTODOS PARA PERFIL COMPLETO (UsuarioPerfilDTO) ===

    public UsuarioPerfilDTO obtenerPerfilCompletoPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        return convertirAUsuarioPerfilDTO(usuario);
    }

    public UsuarioPerfilDTO obtenerPerfilCompletoPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return convertirAUsuarioPerfilDTO(usuario);
    }

    // UsuarioService.java
    public boolean obtenerPerfilCompletadoPorEmail(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return Boolean.TRUE.equals(u.getPerfilCompletado());
    }


    public void marcarPerfilCompletado(String email, boolean completado) {
        System.out.println("UsuarioService.marcarPerfilCompletado - email: " + email + ", completado: " + completado);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + email));

        System.out.println("Usuario encontrado: " + usuario.getId() + ", estado anterior: " + usuario.getPerfilCompletado());

        usuario.setPerfilCompletado(completado);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        System.out.println("Estado guardado en BD: " + usuarioGuardado.getPerfilCompletado());
    }


    public List<UsuarioPerfilDTO> buscarUsuariosPerfilPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::convertirAUsuarioPerfilDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioPerfilDTO> obtenerPerfilesPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol).stream()
                .map(this::convertirAUsuarioPerfilDTO)
                .collect(Collectors.toList());
    }


    // === MÉTODOS PARA ENTIDAD USUARIO ===

    public List<Usuario> obtenerPorRol(RolUsuario rol) {
        return usuarioRepository.findByRol(rol);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuario> obtenerEntidadPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario asignarRol(Long usuarioId, RolUsuario rol) {
        Usuario u = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        System.out.println("[asignarRol] Usuario: " + u.getEmail());
        System.out.println("[asignarRol] Roles antes: " + u.getRoles());
        System.out.println("[asignarRol] Rol recibido desde frontend: " + rol);

        u.getRoles().clear(); // ✔ limpia roles previos
        u.getRoles().add(rol);

        Usuario guardado = usuarioRepository.save(u);

        System.out.println("[asignarRol] Roles guardados en BD: " + guardado.getRoles());
        return guardado;
    }



    public Usuario actualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // === MÉTODOS PARA FUNCIONALIDADES SOCIALES ===

    @Transactional
    public void seguirUsuario(Long seguidorId, Long seguidoId) {
        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(() -> new RuntimeException("Seguidor no encontrado"));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(() -> new RuntimeException("Usuario a seguir no encontrado"));

        if (seguidorId.equals(seguidoId)) {
            throw new RuntimeException("No puedes seguirte a ti mismo");
        }

        if (usuarioRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId)) {
            throw new RuntimeException("Ya sigues a este usuario");
        }

        seguidor.getSiguiendo().add(seguido);
        usuarioRepository.save(seguidor);
    }

    @Transactional
    public void dejarDeSeguirUsuario(Long seguidorId, Long seguidoId) {
        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(() -> new RuntimeException("Seguidor no encontrado"));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(() -> new RuntimeException("Usuario a dejar de seguir no encontrado"));

        if (!usuarioRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId)) {
            throw new RuntimeException("No sigues a este usuario");
        }

        seguidor.getSiguiendo().remove(seguido);
        usuarioRepository.save(seguidor);
    }

    public List<UsuarioPerfilDTO> obtenerSeguidoresPerfil(Long usuarioId) {
        List<Usuario> seguidores = usuarioRepository.findSeguidoresByUsuarioId(usuarioId);
        return seguidores.stream()
                .map(this::convertirAUsuarioPerfilDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioPerfilDTO> obtenerSiguiendoPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuario.getSiguiendo().stream()
                .map(this::convertirAUsuarioPerfilDTO)
                .collect(Collectors.toList());
    }

    public boolean verificarSiSigo(Long seguidorId, Long seguidoId) {
        return usuarioRepository.existsBySeguidorIdAndSeguidoId(seguidorId, seguidoId);
    }

    public List<UsuarioPerfilDTO> obtenerUsuariosRecomendadosPerfil(Long usuarioId) {
        PageRequest pageable = PageRequest.of(0, 10);
        List<Usuario> recomendados = usuarioRepository.findRecomendadosExcludingUser(usuarioId, pageable);
        return recomendados.stream()
                .map(this::convertirAUsuarioPerfilDTO)
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerEstadisticasUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSeguidores", obtenerSeguidores(usuarioId).size());
        stats.put("totalSiguiendo", obtenerSiguiendo(usuarioId).size());
        stats.put("totalPublicaciones", 0);
        stats.put("fechaRegistro", usuario.getFechaRegistro());
        stats.put("esVerificado", usuario.getRoles().contains(RolUsuario.ADMIN));

        return stats;
    }

    // === MÉTODOS PRIVADOS DE CONVERSIÓN ===

    /**
     * Convierte a UsuarioDTO (simple - para listas básicas)
     */
    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        // Solo campos básicos según tu UsuarioDTO
        return dto;
    }

    /**
     * Convierte a UsuarioPerfilDTO (completo - para perfil social)
     */
    private UsuarioPerfilDTO convertirAUsuarioPerfilDTO(Usuario usuario) {
        UsuarioPerfilDTO dto = new UsuarioPerfilDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setTelefono(usuario.getTelefono());
        dto.setGenero(usuario.getGenero());
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setEdad(usuario.getEdad());
        dto.setCarrera(usuario.getCarrera());
        dto.setSemestre(usuario.getSemestre());
        dto.setFechaRegistro(usuario.getFechaRegistro());


        // Estadísticas sociales
        dto.setTotalSeguidores(obtenerSeguidores(usuario.getId()).size());
        dto.setTotalSiguiendo(obtenerSiguiendo(usuario.getId()).size());

        return dto;
    }

    // Métodos auxiliares para las relaciones
    private List<Usuario> obtenerSeguidores(Long usuarioId) {
        return usuarioRepository.findSeguidoresByUsuarioId(usuarioId);
    }

    private List<Usuario> obtenerSiguiendo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return List.copyOf(usuario.getSiguiendo());
    }

    public PerfilCompletoDTO obtenerMiPerfilCompleto(String emailUsuario) {

        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // ---------- Usuario ----------
        UsuarioPerfilDTO u = new UsuarioPerfilDTO();
        u.setId(usuario.getId());
        u.setNombre(usuario.getNombre());
        u.setEmail(usuario.getEmail());
        u.setTelefono(usuario.getTelefono());
        u.setGenero(usuario.getGenero());
        u.setEdad(usuario.getEdad());
        u.setCarrera(usuario.getCarrera());
        u.setSemestre(usuario.getSemestre());
        u.setFechaRegistro(usuario.getFechaRegistro());
        // si tienes colecciones de seguidores/siguiendo:
        u.setTotalSeguidores(
                usuario.getSeguidores() != null ? usuario.getSeguidores().size() : 0
        );
        u.setTotalSiguiendo(
                usuario.getSiguiendo() != null ? usuario.getSiguiendo().size() : 0
        );

        // ---------- Preferencias ----------
        PreferenciaDTO prefDTO = null;
        var prefOpt = preferenciaRepository.findByUsuario(usuario);
        if (prefOpt.isPresent()) {
            var pref = prefOpt.get();
            prefDTO = new PreferenciaDTO();
            prefDTO.setId(pref.getId());
            prefDTO.setPreferenciaUbicacion(pref.getPreferenciaUbicacion());
            prefDTO.setPresupuestoMax(pref.getPresupuestoMax());
            prefDTO.setCompartirHabitacion(pref.getCompartirHabitacion());
            prefDTO.setFumador(pref.getFumador());
            prefDTO.setMascotasPermitidas(pref.getMascotasPermitidas());
            prefDTO.setHorasDormirPreferidas(pref.getHorasDormirPreferidas());
        }

        // ---------- Intereses ----------
        InteresDTO interesDTO = null;
        var interesOpt = interesRepository.findByUsuario(usuario);

        if (interesOpt.isPresent()) {
            var inte = interesOpt.get();

            interesDTO = new InteresDTO();
            interesDTO.setId(inte.getId());

            interesDTO.setMusica(
                    inte.getMusica() != null ? new ArrayList<>(inte.getMusica()) : null
            );
            interesDTO.setDeportes(
                    inte.getDeportes() != null ? new ArrayList<>(inte.getDeportes()) : null
            );
            interesDTO.setHobbies(
                    inte.getHobbies() != null ? new ArrayList<>(inte.getHobbies()) : null
            );

            interesDTO.setFiestas(inte.getFiestas());
            interesDTO.setNivelLimpieza(inte.getNivelLimpieza());
            interesDTO.setPersonalidad(inte.getPersonalidad());
            interesDTO.setHorarioEstudio(inte.getHorarioEstudio());
        }

        return new PerfilCompletoDTO(u, prefDTO, interesDTO);
    }


}