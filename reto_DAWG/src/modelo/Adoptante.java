package modelo;

public class Adoptante extends Usuario{
	
	private String estado_solicitud;
	
	public Adoptante(String id_usuario, String contraseña, String nombre, String email, String administrador,
			String telefono, String estado_solicitud) {
		super(id_usuario, contraseña, nombre, email, administrador, telefono);
		this.estado_solicitud = estado_solicitud;
	}


	public String getEstado_solicitud() {
		return estado_solicitud;
	}

	public void setEstado_solicitud(String estado_solicitud) {
		this.estado_solicitud = estado_solicitud;
	}

}
