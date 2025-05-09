package resporitorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import modelo.Animal;

public class ResporitorioAnimal {
	public static ArrayList<Animal> mostrarAnimales(){
		ArrayList<Animal> list =new ArrayList<Animal>();
		
		String query="select * from animal";
		try {
			PreparedStatement stmt=ConectorBD.conexion.prepareStatement(query);
			ResultSet rs=stmt.executeQuery();
			
			while(rs.next()) {
				String id_animal=rs.getString("id_animal");
				String nombre =rs.getString("nombre");
				int edad=rs.getInt("edad");
				String raza=rs.getString("raza");
				String historial_medico=rs.getString("historial_medico");
				String necesidades_especiales=rs.getString("necesidades_especiales");
				String estado_adopcion=rs.getString("estado_adopcion");
				String caracteristicas=rs.getString("caracteristicas");
				
				Animal animal=new Animal(id_animal,nombre, edad, raza, historial_medico, necesidades_especiales, estado_adopcion, caracteristicas);
				list.add(animal);
		}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	//id_animal, nombre, edad, raza, historial_medico, necesidades_especiales, estado_adopcion, caracteristicas, fecha_solicitud, id_usuario
	public static void insertarAnimal(Animal animal, String id_usuario, String fech_soli) {
	    String query = "INSERT INTO animal VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    try {
	        PreparedStatement stmt = ConectorBD.conexion.prepareStatement(query);
	        stmt.setString(1, animal.getId_animal());
	        stmt.setString(2, animal.getNombre());
	        stmt.setInt(3, animal.getEdad());
	        stmt.setString(4, animal.getRaza());
	        stmt.setString(5, animal.getHistorial_medico());
	        stmt.setString(6, animal.getNecisidades_especiales());
	        stmt.setString(7, animal.getEstado_adopcion());
	        stmt.setString(8, animal.getCaracteristicas());
	        stmt.setString(9, fech_soli);
	        stmt.setString(10, id_usuario);
	        
	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        System.err.println("Error al insertar animal: " + e.getMessage());
	    }
	}

	
	public static ArrayList<Animal> buscarAnimal(String razaa){
		
		String razaaa="%"+razaa+"%";
		String query="select * from animal where raza like ?";
		ArrayList<Animal> lista=new ArrayList<Animal>();
		
		try {
			PreparedStatement stmt=ConectorBD.conexion.prepareStatement(query);
			stmt.setString(1, razaaa);
			ResultSet rs=stmt.executeQuery();
			
			while(rs.next()) {
				String id_animal=rs.getString("id_animal");
				String nombre =rs.getString("nombre");
				int edad=rs.getInt("edad");
				String raza=rs.getString("raza");
				String historial_medico=rs.getString("historial_medico");
				String necesidades_especiales=rs.getString("necesidades_especiales");
				String estado_adopcion=rs.getString("estado_adopcion");
				String caracteristicas=rs.getString("caracteristicas");
				
				Animal animal=new Animal(id_animal,nombre, edad, raza, historial_medico, necesidades_especiales, estado_adopcion, caracteristicas);
				lista.add(animal);
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lista;
	}
	
	public static void DelateAnimal() {
		String query="delete from animal where estado_adopcion like ?";
		String adopcion="%adoptado%";
		try {
			PreparedStatement stmt=ConectorBD.conexion.prepareStatement(query);
			stmt.setString(1, adopcion);
			stmt.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
}
