package com.codesdfc.backend_uni_stay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alojamientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alojamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private Double precio;
    private Double distancia;
    private Integer habitaciones;
    private String disponibilidad;
    private Double calificacion;
    private String direccion;
    private Double longitud;
    private Double latitud;

    private Long visitas; // contador de popularidad


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario-alojamientos")
    private Usuario publicador;

    @ElementCollection
    private Set<String> fotos = new HashSet<>();

    @ElementCollection
    private Set<String> caracteristicas = new HashSet<>();

    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "alojamiento-solicitudes")
    private Set<Solicitud> solicitudes = new HashSet<>();


    @CreationTimestamp
    @Column(name = "fecha_publicacion", updatable = false, nullable = false)
    private LocalDateTime fechaPublicacion;
}
