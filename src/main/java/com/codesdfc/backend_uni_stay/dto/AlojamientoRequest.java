package com.codesdfc.backend_uni_stay.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AlojamientoRequest {
    private String nombre;
    private String descripcion;
    private Double precio;
    private Double distancia;
    private Integer habitaciones;
    private String disponibilidad;
    private Double calificacion;
    private String direccion;
    private Double longitud;
    private Double latitud;
    private Set<String> fotos;
    private Set<String> caracteristicas;
}