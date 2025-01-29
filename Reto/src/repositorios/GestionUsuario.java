package repositorios;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import clases.Usuario;
import clases.Vivienda;
import view.MenuOficina;
import view.MenuVivienda;

public class GestionUsuario {
	
	private final static Scanner sc= new Scanner (System.in);
	
	private static String dniUsuario; // Aquí guardas el DNI del usuario logueado

    public static String getDniUsuario() {
        return dniUsuario;  // Devuelves el DNI cuando se necesite
    }
	
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
	    String consulta = "SELECT * FROM usuario WHERE email=? AND Contraseña=?";
	    try {
	        PreparedStatement statement = ConectorBD.conexion.prepareStatement(consulta);
	        statement.setString(1, email);
	        statement.setString(2, Contraseña);
	        ResultSet rs = statement.executeQuery();

	        if (!rs.next()) {
	            System.out.println("Usuario o contraseña incorrectos.");
	        } else {
	            String rol = rs.getString("Rol");
	            dniUsuario = rs.getString("DNI");
	            if ("Administrador".equalsIgnoreCase(rol)) {
	                System.out.println("Bienvenido Administrador.");
	                MenuVivienda.mostrarMenuVivienda(sc);
	            } else if ("Cliente".equalsIgnoreCase(rol)) {
	                System.out.println("Bienvenido Cliente.");
	                MenuOficina.menuOficina(sc);
	            } else {
	                System.out.println("Usuario o contraseña incorrectos.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error al realizar el login.");
	    }
	}

}
