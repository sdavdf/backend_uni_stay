package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilCompletoDTO {
    private UsuarioPerfilDTO usuario;
    private PreferenciaDTO preferencia;
    private InteresDTO interes;


}