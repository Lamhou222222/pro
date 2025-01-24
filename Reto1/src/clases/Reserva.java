package clases;

import java.sql.Date;

public class Reserva {
	private Date fechaEntrada;
	private Date fechaSalida;
	private int numHuespedes;
	private double totalPagado;
	
	public Reserva() {
		super();
	}

	public Reserva(Date fechaEntrada, Date fechaSalida, int numHuespedes, double totalPagado) {
		super();
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.numHuespedes = numHuespedes;
		this.totalPagado = totalPagado;
	}

	public Date getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(Date fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public Date getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(Date fechaSalida) {
		this.fechaSalida = fechaSalida;
	}

	public int getNumHuespedes() {
		return numHuespedes;
	}

	public void setNumHuespedes(int numHuespedes) {
		this.numHuespedes = numHuespedes;
	}

	public double getTotalPagado() {
		return totalPagado;
	}

	public void setTotalPagado(double totalPagado) {
		this.totalPagado = totalPagado;
	}
	
	
	
	
	
	
	
}
