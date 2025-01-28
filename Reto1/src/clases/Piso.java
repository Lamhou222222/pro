package clases;

public class Piso extends Vivienda {
	
	//Atributos
	private int dias;

	//Constructor vacío
	public Piso() {
		super();
	}
	
	//Constructor con atributos
	public Piso(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia,
			int dias) {
		super();
		this.dias = dias;
	}
	
	//Getters y Setters
	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}
	
	//ToString
	@Override
	public String toString() {
		return "Piso [Días: " + dias + ", CodVivienda: " + codVivienda + ", Ciudad: " + ciudad + ", Dirección: " + direccion
				+ ", NumHab: " + numHab + ", Descripción: " + descripcion + ", Precio-Dia: " + precioDia + "]";
	}
	
}
