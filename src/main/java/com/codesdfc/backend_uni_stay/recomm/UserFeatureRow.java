package com.codesdfc.backend_uni_stay.recomm;

public class UserFeatureRow {

    private Long userId;
    private String nombre;
    private String genero;

    private String personalidad;
    private String nivelLimpieza;
    private String horarioEstudio;

    private Boolean compartirHabitacion;
    private Boolean fumador;
    private Boolean mascotasPermitidas;
    private Boolean fiestas;

    private Integer edad;
    private Double presupuestoMax;
    private Double distanciaMaxima;

    private String tipoAlojamientoPreferido;
    private String preferenciaUbicacion;

    public UserFeatureRow(
            Long userId,
            String nombre,
            String genero,
            String personalidad,
            String nivelLimpieza,
            String horarioEstudio,
            Boolean compartirHabitacion,
            Boolean fumador,
            Boolean mascotasPermitidas,
            Boolean fiestas,
            Integer edad,
            Double presupuestoMax,
            Double distanciaMaxima,
            String tipoAlojamientoPreferido,
            String preferenciaUbicacion
    ) {
        this.userId = userId;
        this.nombre = nombre;
        this.genero = genero;
        this.personalidad = personalidad;
        this.nivelLimpieza = nivelLimpieza;
        this.horarioEstudio = horarioEstudio;
        this.compartirHabitacion = compartirHabitacion;
        this.fumador = fumador;
        this.mascotasPermitidas = mascotasPermitidas;
        this.fiestas = fiestas;
        this.edad = edad;
        this.presupuestoMax = presupuestoMax;
        this.distanciaMaxima = distanciaMaxima;
        this.tipoAlojamientoPreferido = tipoAlojamientoPreferido;
        this.preferenciaUbicacion = preferenciaUbicacion;
    }

    // getters
    public Long getUserId() { return userId; }
    public String getGenero() { return genero; }
    public String getPersonalidad() { return personalidad; }

    public String getNombre() {
        return nombre;
    }

    public String getNivelLimpieza() {
        return nivelLimpieza;
    }

    public String getHorarioEstudio() {
        return horarioEstudio;
    }

    public Boolean getCompartirHabitacion() { return compartirHabitacion; }
    public Boolean getFumador() { return fumador; }
    public Boolean getMascotasPermitidas() { return mascotasPermitidas; }
    public Boolean getFiestas() { return fiestas; }
    public Integer getEdad() { return edad; }
    public Double getPresupuestoMax() { return presupuestoMax; }
    public Double getDistanciaMaxima() { return distanciaMaxima; }
    public String getTipoAlojamientoPreferido() { return tipoAlojamientoPreferido; }
    public String getPreferenciaUbicacion() { return preferenciaUbicacion; }
}
