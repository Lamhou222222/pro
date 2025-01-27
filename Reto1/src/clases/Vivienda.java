package clases;

public class Vivienda {
	
	//Atributos
	//protected int codVivienda;
	protected int codVivienda;
	protected String ciudad;
	protected String direccion;
	protected int numHab;
	protected String descripcion;
	protected double precioDia;
	protected String tipo_Vivienda;
	protected int dias;
	protected int semanas;
	
	//Constructor vacío
	public Vivienda() {
		super();
	}

	//Constructor con atributos
	public Vivienda(String ciudad, String direccion, int numHab, String descripcion,
			double precioDia, String tipo_Vivienda, int dias, int semanas) {
		super();
		//this.codVivienda = codVivienda;
		this.ciudad = ciudad;
		this.direccion = direccion;
		this.numHab = numHab;
		this.descripcion = descripcion;
		this.precioDia = precioDia;
		this.tipo_Vivienda=tipo_Vivienda;
		this.dias=dias;
		this.semanas=semanas;
	}

	//Getters y Setters

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

	public String getTipo_Vivienda() {
		return tipo_Vivienda;
	}

	public void setTipo_Vivienda(String tipo_Vivienda) {
		this.tipo_Vivienda = tipo_Vivienda;
	}

	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}

	public int getSemanas() {
		return semanas;
	}

	public void setSemanas(int semanas) {
		this.semanas = semanas;
	}

	
	//ToString

	
	
	
	
	
}
