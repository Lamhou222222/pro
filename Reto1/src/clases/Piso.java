package clases;

public class Piso extends Vivienda {
	private int dias;

	public Piso(int codVivienda, String ciudad, String direccion, int numHab, String descripcion, double precioDia,
			int dias) {
		super(codVivienda, ciudad, direccion, numHab, descripcion, precioDia);
		this.dias = dias;
	}

	@Override
	public String toString() {
		return "Piso [dias=" + dias + ", codVivienda=" + codVivienda + ", ciudad=" + ciudad + ", direccion=" + direccion
				+ ", numHab=" + numHab + ", descripcion=" + descripcion + ", precioDia=" + precioDia + "]";
	}
	
	


	

	
	
	
}
