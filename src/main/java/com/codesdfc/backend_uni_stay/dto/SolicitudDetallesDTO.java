package com.codesdfc.backend_uni_stay.dto;

import java.time.LocalDateTime;

public class SolicitudDetallesDTO {

    public Long id;
    public String mensaje;
    public LocalDateTime fechaSolicitud;
    public LocalDateTime fechaRespuesta;
    public String estado;
    public String motivoRechazo;
    public Double ofertaPrecio;

    // Solicitante
    public Long solicitanteId;
    public String solicitanteNombre;
    public String solicitanteEmail;
    public String solicitanteTelefono;

    // Alojamiento
    public Long alojamientoId;
    public String alojamientoNombre;

    // Publicador del alojamiento (dueño real del aviso)
    public Long publicadorId;
    public String publicadorNombre;
    public String publicadorEmail;
    public String publicadorTelefono;
}
