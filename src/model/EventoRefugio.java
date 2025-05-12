package model;

import java.sql.Timestamp;

public class EventoRefugio {
    private int id;
    private String nombreEvento;
    private String descripcionEvento;
    private Timestamp fechaInicioEvento;
    private Timestamp fechaFinEvento;
    private String ubicacion;
    private String tipoEvento;
    private Integer idUsuarioOrganizador;

    public EventoRefugio() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombreEvento() { return nombreEvento; }
    public void setNombreEvento(String nombreEvento) { this.nombreEvento = nombreEvento; }
    public String getDescripcionEvento() { return descripcionEvento; }
    public void setDescripcionEvento(String descripcionEvento) { this.descripcionEvento = descripcionEvento; }
    public Timestamp getFechaInicioEvento() { return fechaInicioEvento; }
    public void setFechaInicioEvento(Timestamp fechaInicioEvento) { this.fechaInicioEvento = fechaInicioEvento; }
    public Timestamp getFechaFinEvento() { return fechaFinEvento; }
    public void setFechaFinEvento(Timestamp fechaFinEvento) { this.fechaFinEvento = fechaFinEvento; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public Integer getIdUsuarioOrganizador() { return idUsuarioOrganizador; }
    public void setIdUsuarioOrganizador(Integer idUsuarioOrganizador) { this.idUsuarioOrganizador = idUsuarioOrganizador; }

    @Override
    public String toString() {
        return nombreEvento + " - " + fechaInicioEvento.toLocalDateTime().toLocalDate();
    }
}