package modelo;

public class Usuario {
	private String id_usuario;
	private String contraseña;
	private String nombre;
	private String email;
	private String administrador;
	private String telefono;
	
	public Usuario(String id_usuario, String contraseña, String nombre, String email, String administrador,
			String telefono) {
		this.id_usuario = id_usuario;
		this.contraseña = contraseña;
		this.nombre = nombre;
		this.email = email;
		this.administrador = administrador;
		this.telefono = telefono;
	}
	
	public String getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(String id_usuario) {
		this.id_usuario = id_usuario;
	}

	public String getContraseña() {
		return contraseña;
	}

	public void setContraseña(String contraseña) {
		this.contraseña = contraseña;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAdministrador() {
		return administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	
}
