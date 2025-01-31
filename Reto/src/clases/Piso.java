package clases;

public class Piso extends Vivienda {
	
	//Atributos
	private String planta;

	//Constructor vacío
	public Piso() {
		super();
	}
	
	//Constructor con atributos
	public Piso(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia,
			String planta) {
		super();
		this.planta = planta;
	}
	
	//Getters y Setters
	public String getDias() {
		return planta;
	}

	public void setDias(String planta) {
		this.planta = planta;
	}
	
	//ToString
	@Override
	public String toString() {
		return "Piso [Planta: " + planta + ", Ciudad: " + ciudad + ", Dirección: " + direccion
				+ ", NumHab: " + numHab + ", Descripción: " + descripcion + ", Precio-Dia: " + precioDia + "]";
	}
	
}
