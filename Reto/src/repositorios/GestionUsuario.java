package repositorios;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clases.Usuario;
import view.MenuOficina;
import view.MenuVivienda;

public class GestionUsuario {
	
	private final static Scanner sc= new Scanner (System.in);
	
	private static String dniUsuario; // Aquí guardas el DNI del usuario logueado.

    public static String getDniUsuario() {
        return dniUsuario;  // Devuelves el DNI cuando se necesite.
    }
    public static String nombre;
    public static String getNombre() {
    	return nombre;
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
	    
		   String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		    Pattern pattern = Pattern.compile(regex);
		    Matcher matcher = pattern.matcher(email);

		    // Si el correo electrónico no es válido, terminamos la ejecución y mostramos el mensaje de error.
		    if (!matcher.matches()) {
		        System.out.println("El correo electrónico no es válido.");
		        return;  // Salimos del método si el correo no es válido.
		    }
		
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
	            nombre= rs.getString("Nombre");
	            if ("Administrador".equalsIgnoreCase(rol)) {
	                System.out.println("Bienvenido Administrador.");
	                MenuVivienda.mostrarMenuVivienda(sc);
	            } else if ("Cliente".equalsIgnoreCase(rol)) {
	                System.out.println("Bienvenido "+nombre+".");
	                MenuOficina.menuOficina(sc);
	            } else {
	                System.out.println("Usuario o contraseña incorrectos.");
	                System.out.println();
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error al realizar el login.");
	    }
	}
	public static void mostrarUsuarios() {
		String Select= "SELECT * FROM usuario";
		try {
			PreparedStatement statement=ConectorBD.conexion.prepareStatement(Select);
			ResultSet rs=statement.executeQuery(Select);
			
			while(rs.next()) {
				System.out.println("DNI: "+rs.getString("DNI")+", Nombre: "+rs.getString("Nombre")
				+", Nombre Usuario: "+rs.getString("NomUs")+", Email: "+rs.getString("Email")+", Contraseña: "+rs.getString("Contraseña")
						+", Rol: "+rs.getString("Rol"));
			}			
		} catch (SQLException e) {
			
			e.printStackTrace();
			System.out.println("Error al mostrar los usuarios: "+Select);
		}
		
	}

}
