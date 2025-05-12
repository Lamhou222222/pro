package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Donacion {
    private int id;
    private Integer idUsuarioDonante;
    private String nombreDonanteAnonimo;
    private BigDecimal monto;
    private String tipoDonacion;
    private String descripcionItems;
    private Timestamp fechaDonacion;

    public Donacion() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getIdUsuarioDonante() { return idUsuarioDonante; }
    public void setIdUsuarioDonante(Integer idUsuarioDonante) { this.idUsuarioDonante = idUsuarioDonante; }
    public String getNombreDonanteAnonimo() { return nombreDonanteAnonimo; }
    public void setNombreDonanteAnonimo(String nombreDonanteAnonimo) { this.nombreDonanteAnonimo = nombreDonanteAnonimo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public String getTipoDonacion() { return tipoDonacion; }
    public void setTipoDonacion(String tipoDonacion) { this.tipoDonacion = tipoDonacion; }
    public String getDescripcionItems() { return descripcionItems; }
    public void setDescripcionItems(String descripcionItems) { this.descripcionItems = descripcionItems; }
    public Timestamp getFechaDonacion() { return fechaDonacion; }
    public void setFechaDonacion(Timestamp fechaDonacion) { this.fechaDonacion = fechaDonacion; }
}