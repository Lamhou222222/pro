package modelo;

public class Voluntario extends Usuario {
	
	
	private String Disponibilidad; 
	public Voluntario(String id_usuario, String contraseña, String nombre, String email, String administrador,
			String telefono, String disponibilidad) {
		super(id_usuario, contraseña, nombre, email, administrador, telefono);
		Disponibilidad = disponibilidad;
	}

	
	
	public String getDisponibilidad() {
		return Disponibilidad;
	}

	public void setDisponibilidad(String disponibilidad) {
		Disponibilidad = disponibilidad;
	}

	
}
