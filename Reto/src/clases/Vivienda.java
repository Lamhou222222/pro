package clases;

public class Vivienda {
	
	//Atributos
	protected int codViv;
	protected int idOficina;
	protected String disponible;
	protected String ciudad;
	protected String direccion;
	protected int numHab;
	protected String descripcion;
	protected double precioDia;
	protected String tipo_Vivienda;
	protected String planta;
	protected String piscina;
	
	//Constructor vacío
	public Vivienda() {
		super();
	}

	//Constructor con atributos
	public Vivienda(int idOficina, String disponible, String ciudad, String direccion, int numHab, String descripcion,
			double precioDia, String tipo_Vivienda, String planta, String piscina) {
		super();
		this.idOficina=idOficina;
		this.disponible=disponible;
		this.ciudad = ciudad;	
		this.direccion = direccion;
		this.numHab = numHab;
		this.descripcion = descripcion;
		this.precioDia = precioDia;
		this.tipo_Vivienda=tipo_Vivienda;
		this.planta=planta;
		this.piscina=piscina;
	}

	//Getters y Setters
	
	public int getCodViv() {
		return codViv;
	}

	public void setCodViv(int codViv) {
		this.codViv = codViv;
	}

	public int getIdOficina() {
		return idOficina;
	}


	public void setIdOficina(int idOficina) {
		this.idOficina = idOficina;
	}
	
	public String getDisponible() {
		return disponible;
	}

	public void setDisponible(String disponible) {
		this.disponible = disponible;
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

	public String getTipo_Vivienda() {
		return tipo_Vivienda;
	}

	public void setTipo_Vivienda(String tipo_Vivienda) {
		this.tipo_Vivienda = tipo_Vivienda;
	}

	public String getPlanta() {
		return planta;
	}

	public void setPlanta(String planta) {
		this.planta = planta;
	}
	

	public String isPiscina() {
		return piscina;
	}

	public void setPiscina(String piscina) {
		this.piscina = piscina;
	}
	
}
