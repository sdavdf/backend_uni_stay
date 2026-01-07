package com.codesdfc.backend_uni_stay.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlojamientoFilterDTO {

    private Long id;
    private String nombre;
    private Double precio;
    private Double distancia;
    private Integer habitaciones;
    private String direccion;

    private Long publicadorId;
    private String publicadorNombre;

    private LocalDateTime fechaPublicacion;
}
