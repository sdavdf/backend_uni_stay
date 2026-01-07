package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.dto.InteresDTO;
import com.codesdfc.backend_uni_stay.model.Interes;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.InteresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteresService {

    private final InteresRepository interesRepository;

    // ====== CRUD básico / consultas ======

    public Optional<Interes> obtenerPorUsuario(Usuario usuario) {
        return interesRepository.findByUsuario(usuario);
    }

    public Optional<Interes> obtenerPorUsuarioId(Long usuarioId) {
        return interesRepository.findByUsuarioId(usuarioId);
    }

    public List<Interes> buscarInteresesSimilares(String personalidad, String nivelLimpieza, String horarioEstudio) {
        return interesRepository.findWithSimilarInterests(personalidad, nivelLimpieza, horarioEstudio);
    }

    public List<Interes> buscarPorHobbies(Collection<String> hobbies) {
        return interesRepository.findByHobbiesIn(hobbies);
    }

    public void eliminarIntereses(Long interesId) {
        interesRepository.deleteById(interesId);
    }

    public void eliminarInteresesPorUsuario(Usuario usuario) {
        interesRepository.findByUsuario(usuario)
                .ifPresent(i -> interesRepository.deleteById(i.getId()));
    }

    // ====== guardar/actualizar intereses de un usuario ======

    public Interes guardarOActualizarIntereses(Usuario usuario, InteresDTO dto) {
        Interes interes = interesRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Interes nuevo = new Interes();
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        // Copia EXACTA de lo que viene del DTO
        interes.setMusica(
                dto.getMusica() != null ? new ArrayList<>(dto.getMusica()) : new ArrayList<>()
        );
        interes.setDeportes(
                dto.getDeportes() != null ? new ArrayList<>(dto.getDeportes()) : new ArrayList<>()
        );
        interes.setHobbies(
                dto.getHobbies() != null ? new ArrayList<>(dto.getHobbies()) : new ArrayList<>()
        );

        interes.setFiestas(dto.getFiestas());
        interes.setNivelLimpieza(dto.getNivelLimpieza());
        interes.setPersonalidad(dto.getPersonalidad());
        interes.setHorarioEstudio(dto.getHorarioEstudio());

        return interesRepository.save(interes);
    }


    public InteresDTO mapToDTO(Interes interes) {
        if (interes == null) return null;

        InteresDTO dto = new InteresDTO();
        dto.setId(interes.getId());
        dto.setMusica(
                interes.getMusica() != null ? new ArrayList<>(interes.getMusica()) : null
        );
        dto.setDeportes(
                interes.getDeportes() != null ? new ArrayList<>(interes.getDeportes()) : null
        );
        dto.setHobbies(
                interes.getHobbies() != null ? new ArrayList<>(interes.getHobbies()) : null
        );
        dto.setFiestas(interes.getFiestas());
        dto.setNivelLimpieza(interes.getNivelLimpieza());
        dto.setPersonalidad(interes.getPersonalidad());
        dto.setHorarioEstudio(interes.getHorarioEstudio());

        return dto;
    }
}
