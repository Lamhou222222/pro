package model;

import java.sql.Timestamp;

public class Animal {
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private Integer edadEstimadaAnios;
    private Integer edadEstimadaMeses;
    private String genero;
    private String color;
    private String tamanio;
    private String descripcionCaracter;
    private String historialMedico;
    private String necesidadesEspeciales;
    private Timestamp fechaIngreso;
    private String fotoUrl;
    private EstadoAdopcion estadoAdopcion;
    private Integer idUsuarioResponsable;

    public enum EstadoAdopcion {
        DISPONIBLE, ADOPTADO, EN_PROCESO_DE_ADOPCION, RESERVADO
    }

    public Animal() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }
    public Integer getEdadEstimadaAnios() { return edadEstimadaAnios; }
    public void setEdadEstimadaAnios(Integer edadEstimadaAnios) { this.edadEstimadaAnios = edadEstimadaAnios; }
    public Integer getEdadEstimadaMeses() { return edadEstimadaMeses; }
    public void setEdadEstimadaMeses(Integer edadEstimadaMeses) { this.edadEstimadaMeses = edadEstimadaMeses; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }
    public String getDescripcionCaracter() { return descripcionCaracter; }
    public void setDescripcionCaracter(String descripcionCaracter) { this.descripcionCaracter = descripcionCaracter; }
    public String getHistorialMedico() { return historialMedico; }
    public void setHistorialMedico(String historialMedico) { this.historialMedico = historialMedico; }
    public String getNecesidadesEspeciales() { return necesidadesEspeciales; }
    public void setNecesidadesEspeciales(String necesidadesEspeciales) { this.necesidadesEspeciales = necesidadesEspeciales; }
    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public EstadoAdopcion getEstadoAdopcion() { return estadoAdopcion; }
    public void setEstadoAdopcion(EstadoAdopcion estadoAdopcion) { this.estadoAdopcion = estadoAdopcion; }
    public Integer getIdUsuarioResponsable() { return idUsuarioResponsable; }
    public void setIdUsuarioResponsable(Integer idUsuarioResponsable) { this.idUsuarioResponsable = idUsuarioResponsable; }

    @Override
    public String toString() { // Para JComboBox o listas
        return nombre + " (" + especie + (raza != null ? ", " + raza : "") + ")";
    }
}