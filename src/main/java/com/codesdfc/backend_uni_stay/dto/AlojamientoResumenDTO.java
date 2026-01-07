// src/main/java/com/codesdf/unimatch/dto/AlojamientoResumenDTO.java
package com.codesdfc.backend_uni_stay.dto;

import com.codesdfc.backend_uni_stay.model.Alojamiento;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AlojamientoResumenDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private String direccion;
    // Si luego quieres enviar un “tipo”, agrega un campo String tipo;
    private String primeraFoto;

    private Long propietarioId;
    private String propietarioNombre;
    private String propietarioEmail;
    private String propietarioTelefono;

    public static AlojamientoResumenDTO from(Alojamiento a) {
        if (a == null) return null;

        String firstPhoto = null;
        try {
            var fotos = a.getFotos(); // ajusta si tu entidad guarda fotos distinto
            if (fotos != null && !fotos.isEmpty()) {
                firstPhoto = fotos.iterator().next(); // sirve para List/Set
            }
        } catch (Exception ignored) {}

        return AlojamientoResumenDTO.builder()
                .id(a.getId())
                .nombre(a.getNombre())
                .precio(a.getPrecio())
                .direccion(a.getDireccion())
                // .tipo( a.getTipo() ) // <- Si tu entidad tiene "tipo", puedes descomentar y crear el campo
                .primeraFoto(firstPhoto)
                .propietarioId(a.getPublicador() != null ? a.getPublicador().getId() : null)
                .propietarioNombre(a.getPublicador() != null ? a.getPublicador().getNombre() : null)
                .propietarioEmail(a.getPublicador() != null ? a.getPublicador().getEmail() : null)
                .propietarioTelefono(a.getPublicador() != null ? a.getPublicador().getTelefono() : null)
                .build();
    }
}
