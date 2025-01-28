package repositorios;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import clases.Vivienda;


public class GestionVivienda {
	
    private Scanner scanner = new Scanner(System.in);

    public static void insertarVivienda(Vivienda vivienda) {
        
            String insert = "INSERT INTO vivienda (idOficina, ciudad, direccion, descripcion, num_hab, precio_dia,Tipo_Vivienda,Dias,Semanas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
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
    public static void mostrarViviendasBD() {
            	System.out.println("Lista de viviendas");
                   String Select = "SELECT * FROM mr_robot.vivienda";
                	try {
        				PreparedStatement statement=ConectorBD.conexion.prepareStatement(Select);
        				ResultSet rs=statement.executeQuery(Select);
        				
        				while(rs.next()) {
        					System.out.println("Codigo Vivienda: "+rs.getInt("CodVivienda")+
        							", Ciudad: "+rs.getString("Ciudad")+", Dirección: "+rs.getString("Dirección")
        							+", Numero Habitantes: "+rs.getInt("NumHab")+", Descripción: "+rs.getString("descripcion")
        							+", Precio/dia: "+rs.getDouble("precioDia")+", Tipo Vivienda: "+rs.getString("tipoVivienda")
        							+", Dias: "+rs.getInt("dias")+", Semanas: "+rs.getInt("semanas"));
        				}
        			} catch (SQLException e) {
        				
        				e.printStackTrace();
        				System.out.println("Error al hacer la consulta: "+Select);
        			}
            }
                    
  
}

