package com.codesdfc.backend_uni_stay.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Getter @Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    private String nombre;
    private String email;
    private String password;
    private String telefono;

    // Perfil social
    private String genero;
    private Integer edad;
    private String carrera;
    private Integer semestre;
    private String bio;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaConexion;
    private Boolean perfilCompletado = false;

    // Roles del usuario
    @ElementCollection(targetClass = RolUsuario.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "roles_usuarios", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Set<RolUsuario> roles = new HashSet<>();


    // Quienes me siguen (inversa)
    @ManyToMany(mappedBy = "siguiendo")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Usuario> seguidores = new HashSet<>();

    // A quiénes sigo
    @ManyToMany
    @JoinTable(
            name = "seguidores",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id")
    )
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Usuario> siguiendo = new HashSet<>();

    // Relaciones con otras entidades
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Preferencia preferencias;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Interes intereses;

    @OneToMany(mappedBy = "publicador", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Alojamiento> alojamientos = new HashSet<>();

    @OneToMany(mappedBy = "solicitante", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Solicitud> solicitudesEnviadas = new HashSet<>();

    @OneToMany(mappedBy = "receptor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Solicitud> solicitudesRecibidas = new HashSet<>();
}
