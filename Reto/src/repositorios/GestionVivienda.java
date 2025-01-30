package repositorios;


import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import clases.Vivienda;



public class GestionVivienda {
	
	
    public static void insertarVivienda(Vivienda vivienda) {
        
            String insert = "INSERT INTO vivienda (idOficina, ciudad, disponible, direccion, descripcion, numhab, precio_dia, Tipo_Vivienda, planta, piscina) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try ( 
                 PreparedStatement statement = ConectorBD.conexion.prepareStatement(insert)) {
               
            	statement.setInt(1, vivienda.getIdOficina());
                statement.setString(2, vivienda.getCiudad());
                statement.setBoolean(3, vivienda.isDisponible());
                statement.setString(4, vivienda.getDireccion());
                statement.setString(5, vivienda.getDescripcion());
                statement.setInt(6,vivienda.getNumHab() );
                statement.setDouble(7,vivienda.getPrecioDia() );
                statement.setString(8,vivienda.getTipo_Vivienda());
                statement.setString(9,vivienda.getPlanta());
                statement.setBoolean(10,vivienda.isPiscina());
                
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
        					System.out.println("Codigo Vivienda: "+rs.getInt("CodVivienda")+", IdOficina: "+rs.getInt("IdOficina")+
        							", Ciudad: "+rs.getString("Ciudad")+", Direccion: "+rs.getString("Direccion")
        							+", Numero Habitantes: "+rs.getInt("NumHab")+", Descripción: "+rs.getString("descripcion")
        							+", Precio/dia: "+rs.getDouble("Precio_Dia")+", Tipo Vivienda: "+rs.getString("Tipo_Vivienda")
        							+", Dias: "+rs.getInt("Dias")+", Semanas: "+rs.getInt("Semanas"));
        				}			
        			} catch (SQLException e) {
        				
        				e.printStackTrace();
        				System.out.println("Error al hacer la consulta: "+Select);
        			}
            }
                    
}

