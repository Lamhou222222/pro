package repositorios;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.Usuario;
import view.MenuVivienda;

public class GestionUsuario {
	

	public static void insertarUsuario(Usuario usuario) {
		String insert="INSERT INTO Usuario (DNI, Nombre, Apellido, NomUs, Email, Contraseña, Rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement=ConectorBD.conexion.prepareStatement(insert);
			statement.setString(1, usuario.getDni());
			statement.setString(2, usuario.getNombre());
			statement.setString(3, usuario.getApellido());
			statement.setString(4, usuario.getNomUs());
			statement.setString(5, usuario.getEmail());
			statement.setString(6, usuario.getContraseña());
			statement.setString(7, usuario.getRol());
				
			statement.executeUpdate();
				
		} catch (SQLException e) {
				
			e.printStackTrace();
			System.out.println("Error al hacer la consulta: "+insert);
		}
			
	}
	public static void loginUsuario(String email, String Contraseña) {
		
		String consulta= "SELECT * FROM usuario WHERE email=? AND Contraseña=?";
		try {
			PreparedStatement statement=ConectorBD.conexion.prepareStatement(consulta);
			statement.setString(1, email);
			statement.setString(2, Contraseña);
			ResultSet rs=statement.executeQuery();
			
			while(rs.next()) {
				if(email.equals("ikdgg@plaiaundi.net") || email.equals("ikdgs@plaiaundi.net")) {
					MenuVivienda.mostrarMenuVivienda(null);
				}else if (!rs.next()) {
					    System.out.println("Usuario o contraseña incorrectos.");
					    return;

					//MenuOficina.mostrarMenuOficina();
					
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("Error al hacer login: "+consulta);
		}
	}
}
