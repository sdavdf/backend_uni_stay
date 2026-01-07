package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDTO {
    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private Long alojamientoId;
    private String nombreAlojamiento;
    private LocalDateTime fechaSolicitud;
    private String estado;
    private String comentarios;
}