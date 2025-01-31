package repositorios;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.Reserva;

public class GestionReserva {
	
	public static void insertarReserva(Reserva reserva) {
        
        String insert = "INSERT INTO reserva (DniUsuario, CodVivienda, FechaEntrada, FechaSalida, NumHuespedes, TotalPagado) VALUES (?, ?, ?, ?, ?,?)";
        
        try ( 
             PreparedStatement statement = ConectorBD.conexion.prepareStatement(insert)) {
           
        	statement.setString(1, reserva.getDniUsuario());
        	statement.setInt(2, reserva.getCodVivienda());
            statement.setDate(3, reserva.getFechaEntrada());
            statement.setDate(4, reserva.getFechaSalida());
            statement.setInt(5, reserva.getNumHuespedes());
            statement.setDouble(6, reserva.getTotalPagado());
            
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
	public static void mostrarReservas() {
    	System.out.println("Lista de viviendas");
           String Select = "SELECT * FROM mr_robot.reserva WHERE dniUsuario=?";
          
        	try {
				PreparedStatement statement=ConectorBD.conexion.prepareStatement(Select);
				statement.setString(1, GestionUsuario.getDniUsuario());
				ResultSet rs=statement.executeQuery(Select);
				
				while(rs.next()) {
					System.out.println("Codigo Vivienda: "+rs.getInt("CodVivienda")+", IdOficina: "+rs.getInt("IdOficina")+
							", Ciudad: "+rs.getString("Ciudad")+", Direccion: "+rs.getString("Direccion")
							+", Numero Habitantes: "+rs.getInt("NumHab")+", Descripción: "+rs.getString("descripcion")
							+", Precio/dia: "+rs.getDouble("Precio_Dia")+", Tipo Vivienda: "+rs.getString("Tipo_Vivienda")
							+", Planta: "+rs.getString("Planta")+", Piscina: "+rs.getString("Piscina"));
				}			
			} catch (SQLException e) {
				
				e.printStackTrace();
				System.out.println("Error al hacer la consulta: "+Select);
			}
    }

}
