package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEmparejamientoDTO {
    private String nombre;
    private String carrera;
    private String genero;
    private Double presupuestoMax;
    private Boolean fumador;
    private Boolean mascotasPermitidas;
    private String horasDormirPreferidas;
    private Boolean compartirHabitacion;
    private Set<String> musica;
    private Set<String> deportes;
    private Set<String> hobbies;
    private Boolean fiestas;
    private String personalidad;  // Introvertido, Extrovertido, etc.
    private String horarioEstudio;  // mañana, tarde, noche

}

