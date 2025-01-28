package clases;

public class Villa extends Vivienda {
	
	//Atributos
	private int semanas;
	
	//Constructor vacío
	public Villa() {
		super();
	}

	//Constructor con atributos
	public Villa(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia,
			int semanas) {
		super();
		this.semanas = semanas;
	}
	
	//Getters y Setters
	public int getSemanas() {
		return semanas;
	}

	public void setSemanas(int semanas) {
		this.semanas = semanas;
	}

	//ToString
	@Override
	public String toString() {
		return "Villa [semanas=" + semanas + ", codVivienda=" + codVivienda + ", ciudad=" + ciudad + ", direccion="
				+ direccion + ", numHab=" + numHab + ", descripcion=" + descripcion + ", precioDia=" + precioDia + "]";
	}

	
	
	

}
