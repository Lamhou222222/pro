package model;

import java.sql.Timestamp;

public class SolicitudAdopcion {
    private int id;
    private int idAnimal;
    private int idUsuarioSolicitante;
    private Timestamp fechaSolicitud;
    private EstadoSolicitud estado;
    private String motivacion;
    private String notasAdmin;

    public enum EstadoSolicitud {
        ENVIADA, EN_REVISION, APROBADA, RECHAZADA, CANCELADA_POR_USUARIO, ENTREVISTA_PROGRAMADA,EN_PROCESO_DE_ADOPCION,RESERVADO
    }

    public SolicitudAdopcion() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdAnimal() { return idAnimal; }
    public void setIdAnimal(int idAnimal) { this.idAnimal = idAnimal; }
    public int getIdUsuarioSolicitante() { return idUsuarioSolicitante; }
    public void setIdUsuarioSolicitante(int idUsuarioSolicitante) { this.idUsuarioSolicitante = idUsuarioSolicitante; }
    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }
    public String getMotivacion() { return motivacion; }
    public void setMotivacion(String motivacion) { this.motivacion = motivacion; }
    public String getNotasAdmin() { return notasAdmin; }
    public void setNotasAdmin(String notasAdmin) { this.notasAdmin = notasAdmin; }
}