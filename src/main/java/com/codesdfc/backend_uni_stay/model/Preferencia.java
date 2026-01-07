package com.codesdfc.backend_uni_stay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "preferencias")
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Usuario usuario;

    private String preferenciaUbicacion;
    private Double presupuestoMax;
    private Boolean compartirHabitacion;
    private Boolean fumador;
    private Boolean mascotasPermitidas;
    private String horasDormirPreferidas;
    private Double distanciaMaxima;
    private String tipoAlojamientoPreferido;
}
