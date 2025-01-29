package clases;

import java.sql.Date;

public class Reserva {
	
	//Atributos
	private String fechaEntrada;
	private String fechaSalida;
	private int numHuespedes;
	private double totalPagado;
	
	//Constructor vacío
	public Reserva() {
		super();
	}

	//Constructor con atributos
	public Reserva(String fechaEntrada, String fechaSalida, int numHuespedes, double totalPagado) {
		super();
		this.fechaEntrada = fechaEntrada;
		this.fechaSalida = fechaSalida;
		this.numHuespedes = numHuespedes;
		this.totalPagado = totalPagado;
	}
	
	//Getters y Setters
	public String getFechaEntrada() {
		return fechaEntrada;
	}

	public void setFechaEntrada(String fechaEntrada) {
		this.fechaEntrada = fechaEntrada;
	}

	public String getFechaSalida() {
		return fechaSalida;
	}

	public void setFechaSalida(String fechaSalida) {
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
