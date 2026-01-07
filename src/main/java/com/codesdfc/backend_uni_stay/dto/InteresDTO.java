package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteresDTO {
    private Long id;
    private List<String> musica;
    private List<String> deportes;
    private List<String> hobbies;
    private Boolean fiestas;
    private String nivelLimpieza;
    private String personalidad;
    private String horarioEstudio;

}