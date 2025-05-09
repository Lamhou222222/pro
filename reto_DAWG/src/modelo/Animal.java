package modelo;

public class Animal {
	private String Id_animal;
	private String Nombre;
	private int edad;
	private String Raza;
	private String Historial_medico;
	private String Necisidades_especiales;
	private String estado_adopcion;
	private String caracteristicas;
	
	public Animal(String id_animal, String nombre, int edad, String raza, String historial_medico,
			String necisidades_especiales, String estado_adopcion, String caracteristicas) {
		Id_animal = id_animal;
		Nombre = nombre;
		this.edad = edad;
		Raza = raza;
		Historial_medico = historial_medico;
		Necisidades_especiales = necisidades_especiales;
		this.estado_adopcion = estado_adopcion;
		this.caracteristicas = caracteristicas;//
	}
	
	public String getId_animal() {
		return Id_animal;
	}

	public void setId_animal(String id_animal) {
		Id_animal = id_animal;
	}

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		Nombre = nombre;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getRaza() {
		return Raza;
	}

	public void setRaza(String raza) {
		Raza = raza;
	}

	public String getHistorial_medico() {
		return Historial_medico;
	}

	public void setHistorial_medico(String historial_medico) {
		Historial_medico = historial_medico;
	}

	public String getNecisidades_especiales() {
		return Necisidades_especiales;
	}

	public void setNecisidades_especiales(String necisidades_especiales) {
		Necisidades_especiales = necisidades_especiales;
	}

	public String getEstado_adopcion() {
		return estado_adopcion;
	}

	public void setEstado_adopcion(String estado_adopcion) {
		this.estado_adopcion = estado_adopcion;
	}

	public String getCaracteristicas() {
		return caracteristicas;
	}

	public void setCaracteristicas(String caracteristicas) {
		this.caracteristicas = caracteristicas;
	}
	
	
	
}
