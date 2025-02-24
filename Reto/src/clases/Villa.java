package clases;

public class Villa extends Vivienda {
	
	//Atributos
	private String piscina;
	
	//Constructor vacío
	public Villa() {
		super();
	}

	//Constructor con atributos
	public Villa(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia, String tipo_Vivienda,
			String piscina) {
		super();
		this.piscina = piscina;
	}
	
	//Getters y Setters
	public String getPiscina() {
		return piscina;
	}

	public void setPiscina(String piscina) {
		this.piscina = piscina;
	}

	//ToString
	@Override
	public String toString() {
		return "Villa [piscina=" + piscina + ", ciudad=" + ciudad + ", direccion="
				+ direccion + ", numHab=" + numHab + ", descripcion=" + descripcion + ", precioDia=" + precioDia + "]";
	}

	
	
	

}
