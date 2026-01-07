package com.codesdfc.backend_uni_stay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecoItemDTO {
    private Long id;
    private String nombre;
    private Double similitud;
}