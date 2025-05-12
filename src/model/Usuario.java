package model;

import java.sql.Timestamp;

public class Usuario {
    private int id;
    private String username;
    private String passwordHash; // En TEXTO PLANO, almacenará la contraseña tal cual.
    private String nombreCompleto;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private boolean activo;
    private Timestamp fechaRegistro;
    private String disponibilidadHoraria;
    private String areasInteres;
    private String direccion;
    private String tipoVivienda;
    private Boolean experienciaAnimales;

    public enum RolUsuario {
        ADMINISTRADOR, EMPLEADO, VOLUNTARIO, ADOPTANTE_POTENCIAL
    }

    public Usuario() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getDisponibilidadHoraria() { return disponibilidadHoraria; }
    public void setDisponibilidadHoraria(String disponibilidadHoraria) { this.disponibilidadHoraria = disponibilidadHoraria; }
    public String getAreasInteres() { return areasInteres; }
    public void setAreasInteres(String areasInteres) { this.areasInteres = areasInteres; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getTipoVivienda() { return tipoVivienda; }
    public void setTipoVivienda(String tipoVivienda) { this.tipoVivienda = tipoVivienda; }
    public Boolean getExperienciaAnimales() { return experienciaAnimales; }
    public void setExperienciaAnimales(Boolean experienciaAnimales) { this.experienciaAnimales = experienciaAnimales; }

     @Override
    public String toString() { // Útil para JComboBox si muestras objetos Usuario
        return nombreCompleto + " (@" + username + ")";
    }
}