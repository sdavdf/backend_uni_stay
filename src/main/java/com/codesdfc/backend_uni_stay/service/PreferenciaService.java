package com.codesdfc.backend_uni_stay.service;

import com.codesdfc.backend_uni_stay.model.Preferencia;
import com.codesdfc.backend_uni_stay.model.Usuario;
import com.codesdfc.backend_uni_stay.repository.PreferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PreferenciaService {
    private final PreferenciaRepository preferenciaRepository;

    public Preferencia guardarPreferencias(Preferencia preferencia) {
        return preferenciaRepository.save(preferencia);
    }

    public Optional<Preferencia> obtenerPorUsuario(Usuario usuario) {
        return preferenciaRepository.findByUsuario(usuario);
    }

    public Optional<Preferencia> obtenerPorUsuarioId(Long usuarioId) {
        return preferenciaRepository.findByUsuarioId(usuarioId);
    }

    public List<Preferencia> buscarPreferenciasSimilares(Double presupuestoMin, Double presupuestoMax,
                                                         Boolean compartirHabitacion, Double distanciaMin) {
        return preferenciaRepository.findWithSimilarPreferences(presupuestoMin, presupuestoMax,
                compartirHabitacion, distanciaMin);
    }

    public Preferencia crearPreferenciasPorDefecto(Usuario usuario) {
        Preferencia preferencia = new Preferencia();
        preferencia.setUsuario(usuario);
        preferencia.setPresupuestoMax(1000.0);
        preferencia.setCompartirHabitacion(false);
        preferencia.setFumador(false);
        preferencia.setMascotasPermitidas(false);
        preferencia.setDistanciaMaxima(5.0);
        preferencia.setTipoAlojamientoPreferido("Habitación");

        return preferencia;
    }

    // Eliminar preferencias
    public void eliminarPreferencias(Long preferenciaId) {
        preferenciaRepository.deleteById(preferenciaId);
    }

    // Eliminar preferencias por usuario
    public void eliminarPreferenciasPorUsuario(Usuario usuario) {
        Optional<Preferencia> preferencia = preferenciaRepository.findByUsuario(usuario);
        preferencia.ifPresent(p -> preferenciaRepository.deleteById(p.getId()));
    }
}