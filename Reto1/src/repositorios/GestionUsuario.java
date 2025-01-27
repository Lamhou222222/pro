package repositorios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.Usuario;

public class GestionUsuario {
	

	public static void insertarUsuario(Usuario usuario) {
		String insert="INSERT INTO usuario (DNI, Nombre, Apellido, NomUs, Email, Contraseña, Rol) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement statement=ConectorBD.conexion.prepareStatement(insert);
			statement.setString(1, usuario.getDni());
			statement.setString(2, usuario.getNombre());
			statement.setString(3, usuario.getApellido());
			statement.setString(4, usuario.getNomUs());
			statement.setString(5, usuario.getEmail());
			statement.setString(6, usuario.getContraseña());
			statement.setString(7,  usuario.getRol());
				
			statement.executeUpdate();
				
		} catch (SQLException e) {
				
			e.printStackTrace();
			System.out.println("Error al hacer la consulta: "+insert);
		}
			
	}
	private static void loginUsuario(String NomUs) {
		String consulta= "SELECT * FROM usuario WHERE NomUs=?";
		try {
			PreparedStatement statement=ConectorBD.conexion.prepareStatement(consulta);
			statement.setString(1, NomUs);
			ResultSet rs=statement.executeQuery();
			
			while(rs.next()) {
				//if(Rol=="Admin"){
					//System.out.println("Bienvenido Admin "+NomUs);
				//}else if(Rol=="Usuario") {
					//System.out.println("Bienvenido "+NomUs);
				}
			//}
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("Error al hacer login: "+consulta);
		}
	}
}
