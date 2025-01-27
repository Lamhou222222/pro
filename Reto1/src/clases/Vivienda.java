package clases;

public class Vivienda {
	
	//Atributos
	protected int codVivienda;
	protected String ciudad;
	protected String direccion;
	protected int numHab;
	protected String descripcion;
	protected double precioDia;
	
	//Constructor vacío
	public Vivienda() {
		super();
	}

	//Constructor con atributos
	public Vivienda(int codVivienda, String ciudad, String direccion, int numHab, String descripcion,
			double precioDia) {
		super();
		this.codVivienda = codVivienda;
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.numHab = numHab;
		this.descripcion = descripcion;
		this.precioDia = precioDia;
	}

	//Getters y Setters
	public int getCodVivienda() {
		return codVivienda;
	}

	public void setCodVivienda(int codVivienda) {
		this.codVivienda = codVivienda;
	}

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getNumHab() {
		return numHab;
	}

	public void setNumHab(int numHab) {
		this.numHab = numHab;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecioDia() {
		return precioDia;
	}

	public void setPrecioDia(double precioDia) {
		this.precioDia = precioDia;
	}

	//ToString
	@Override
	public String toString() {
		return "Vivienda [codVivienda=" + codVivienda + ", ciudad=" + ciudad + ", direccion=" + direccion + ", numHab="
				+ numHab + ", descripcion=" + descripcion + ", precioDia=" + precioDia + "]";
	}
	
	
	
	
	
}
