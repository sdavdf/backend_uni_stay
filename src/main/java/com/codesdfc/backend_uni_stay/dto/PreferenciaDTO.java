package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreferenciaDTO {
    private Long id;
    private String preferenciaUbicacion;
    private Double presupuestoMax;
    private Boolean compartirHabitacion;
    private Boolean fumador;
    private Boolean mascotasPermitidas;
    private String horasDormirPreferidas;
}