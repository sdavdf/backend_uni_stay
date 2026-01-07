package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPerfilDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String fotoPerfil;
    private String genero;
    private Integer edad;
    private String carrera;
    private Integer semestre;
    private LocalDateTime fechaRegistro;

    private Integer totalSeguidores;
    private Integer totalSiguiendo;



}