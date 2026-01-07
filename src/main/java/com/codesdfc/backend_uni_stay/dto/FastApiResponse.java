package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FastApiResponse {

    private Long usuario_id;
    private List<RecomendacionDTO> recomendaciones;

}
