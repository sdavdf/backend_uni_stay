package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompaneroDTO {
    private Long id;
    private String nombre;
    private String fotoPerfil;
    private String carrera;
    private Integer semestre;
    private Double compatibilidad;
    private Set<String> interesesComunes;
    private Double presupuesto;
    private String ubicacion;
}