package modelo;

public class Donante extends Usuario {
	
	
	private String tipo_donante;
	private String institucion;
	
	
	public String getTipo_donante() {
		return tipo_donante;
	}
	public void setTipo_donante(String tipo_donante) {
		this.tipo_donante = tipo_donante;
	}
	public String getInstitucion() {
		return institucion;
	}
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}
	
	public Donante(String id_usuario, String contraseña, String nombre, String email, String administrador,
			String telefono, String tipo_donante, String institucion) {
		super(id_usuario, contraseña, nombre, email, administrador, telefono);
		this.tipo_donante = tipo_donante;
		this.institucion = institucion;
	}
}
