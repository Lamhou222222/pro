package clases;

public class Villa extends Vivienda {
	private int semanas;

	public Villa(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia,
			int semanas) {
		super(codVivienda, ciudad, direccion, numHab, descripcion, precioDia);
		this.semanas = semanas;
	}

	@Override
	public String toString() {
		return "Villa [semanas=" + semanas + ", codVivienda=" + codVivienda + ", ciudad=" + ciudad + ", direccion="
				+ direccion + ", numHab=" + numHab + ", descripcion=" + descripcion + ", precioDia=" + precioDia + "]";
	}

	
	
	

}
