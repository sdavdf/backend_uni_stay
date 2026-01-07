package com.codesdfc.backend_uni_stay.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intereses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Interes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference(value = "usuario-intereses")
    private Usuario usuario;

    @ElementCollection
    @CollectionTable(name = "interes_musica", joinColumns = @JoinColumn(name = "interes_id"))
    @Column(name = "genero")
    private List<String> musica = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "interes_deportes", joinColumns = @JoinColumn(name = "interes_id"))
    @Column(name = "deporte")
    private List<String> deportes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "interes_hobbies", joinColumns = @JoinColumn(name = "interes_id"))
    @Column(name = "hobby")
    private List<String> hobbies = new ArrayList<>();

    private Boolean fiestas;
    private String nivelLimpieza;
    private String personalidad;
    private String horarioEstudio;
}
