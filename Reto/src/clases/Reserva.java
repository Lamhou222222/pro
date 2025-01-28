package clases;

import java.sql.Date;

public class Reserva {
	
	//Atributos
	private Date fechaEntrada;
	private Date fechaSalida;
	private int numHuespedes;
	private double totalPagado;
	
	//Constructor vacío
	public Reserva() {
		super();
	}

	//Constructor con atributos
	public Reserva(Date fechaEntrada, Date fechaSalida, int numHuespedes, double totalPagado) {
		super();
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.numHuespedes = numHuespedes;
		this.totalPagado = totalPagado;
	}
	
	//Getters y Setters
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

	//ToString
	@Override
	public String toString() {
		return "Reserva [Fecha Entrada: " + fechaEntrada + ", Fecha Salida: " + fechaSalida + ", Numero de Huespedes: "
				+ numHuespedes + ", Total Pagado: " + totalPagado + "]";
	}
	
	
}
