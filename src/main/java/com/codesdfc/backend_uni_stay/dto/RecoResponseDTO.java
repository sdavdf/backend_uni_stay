package com.codesdfc.backend_uni_stay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoResponseDTO {

    @JsonProperty("usuario_id")
    private Long usuarioId;

    private List<RecoItemDTO> recomendaciones;
}