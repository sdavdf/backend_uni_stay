package com.codesdfc.backend_uni_stay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id")
    @JsonBackReference(value = "usuario-solicitudes-enviadas")
    private Usuario solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receptor_id")
    @JsonBackReference(value = "usuario-solicitudes-recibidas")
    private Usuario receptor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alojamiento_id")
    @JsonBackReference(value = "alojamiento-solicitudes")
    private Alojamiento alojamiento;

    private String mensaje;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaRespuesta;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado; // PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA

    private String motivoRechazo;
    private Double ofertaPrecio;

    public enum EstadoSolicitud {
        PENDIENTE, ACEPTADA, RECHAZADA, CANCELADA
    }
}
