package model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date; // CAMBIO: Usar java.util.Date

public class ActividadVoluntariado {
    private int id;
    private int idVoluntario;
    private Integer idAnimalAsociado;
    private Date fechaActividad; // CAMBIO: java.util.Date
    private String tipoActividad;
    private BigDecimal duracionHoras;
    private String descripcion;

    public ActividadVoluntariado() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdVoluntario() { return idVoluntario; }
    public void setIdVoluntario(int idVoluntario) { this.idVoluntario = idVoluntario; }
    public Integer getIdAnimalAsociado() { return idAnimalAsociado; }
    public void setIdAnimalAsociado(Integer idAnimalAsociado) { this.idAnimalAsociado = idAnimalAsociado; }
    
    public Date getFechaActividad() { // CAMBIO: Devuelve java.util.Date
        return fechaActividad;
    }
    public void setFechaActividad(Date fechaActividad) { // CAMBIO: Acepta java.util.Date
        this.fechaActividad = fechaActividad;
    }
    
    public String getTipoActividad() { return tipoActividad; }
    public void setTipoActividad(String tipoActividad) { this.tipoActividad = tipoActividad; }
    public BigDecimal getDuracionHoras() { return duracionHoras; }
    public void setDuracionHoras(BigDecimal duracionHoras) { this.duracionHoras = duracionHoras; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "ActividadVoluntariado{" +
                "id=" + id +
                ", idVoluntario=" + idVoluntario +
                ", fechaActividad=" + (fechaActividad != null ? new SimpleDateFormat("dd/MM/yyyy").format(fechaActividad) : "N/A") +
                ", tipoActividad='" + tipoActividad + '\'' +
                '}';
    }
}