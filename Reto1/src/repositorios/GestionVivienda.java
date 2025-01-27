package repositorios;


import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.util.Scanner;

import clases.Vivienda;


public class GestionVivienda {
	public static  Connection conexion;
	
    private Scanner scanner = new Scanner(System.in);

    public static void insertarVivienda(Vivienda vivienda) {
        
            String insert = "INSERT INTO vivienda (ciudad, direccion, descripcion, num_hab, precio_dia,Tipo_Vivienda,Dias,Semanas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try ( 
                 PreparedStatement statement = ConectorBD.conexion.prepareStatement(insert)) {

               
                statement.setString(1, vivienda.getCiudad());
                statement.setString(2, vivienda.getDireccion());
                statement.setString(3, vivienda.getDescripcion());
                statement.setInt(4,vivienda.getNumHab() );
                statement.setDouble(5,vivienda.getPrecioDia() );
                statement.setString(6,vivienda.getTipo_Vivienda());
                statement.setInt(7,vivienda.getDias());
                statement.setInt(8,vivienda.getSemanas());
                
                int rowsInserted = statement.executeUpdate();
            
                if (rowsInserted > 0) {
                    System.out.println("¡Vivienda añadida con éxito!");
                }
            }
                catch (SQLException e) {
        			e.printStackTrace();
        			System.out.println("Error al hacer la consulta: "+insert);
        		}
                
            }
  
 
   
       
  

    
}

