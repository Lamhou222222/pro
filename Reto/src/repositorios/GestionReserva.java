package repositorios;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import clases.Reserva;

public class GestionReserva {
	
	public static void insertarReserva(Reserva reserva) {
        
        String insert = "INSERT INTO reserva (DniUsuario, CodVivienda, FechaEntrada, FechaSalida, NumHuespedes, TotalPagado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try ( 
             PreparedStatement statement = ConectorBD.conexion.prepareStatement(insert)) {
           
        	statement.setString(1, reserva.getDniUsuario());
        	statement.setInt(2, reserva.getCodVivienda());
            statement.setDate(3, reserva.getFechaEntrada());
            statement.setDate(4, reserva.getFechaSalida());
            statement.setInt(5, reserva.getNumHuespedes());
            statement.setDouble(6, reserva.getTotalPagado());
            
            int rowsInserted = statement.executeUpdate();
            
            String updateVivienda = "UPDATE vivienda SET disponible = 'No' WHERE CodVivienda = ?";
        
            PreparedStatement statementUpdate = ConectorBD.conexion.prepareStatement(updateVivienda);
            statementUpdate.setInt(1, reserva.getCodVivienda());

            statementUpdate.executeUpdate();

        
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
           String Select = "SELECT * FROM mr_robot.reserva WHERE dniUsuario= ?";
          
        	try {
				PreparedStatement statement=ConectorBD.conexion.prepareStatement(Select);
				statement.setString(1, GestionUsuario.getDniUsuario());
				ResultSet rs=statement.executeQuery();
				
				while(rs.next()) {
					System.out.println("Codigo reserva: "+rs.getInt("CodReserva")+", DNI: "+rs.getString("DniUsuario")+", CodVivienda: "+rs.getInt("CodVivienda")+
							", Fecha Entrada: "+rs.getString("FechaEntrada")+", Fecha Salida: "+rs.getString("FechaSalida")
							+", Numero de Huespedes: "+rs.getInt("NumHuespedes")+", Total a pagar: "+rs.getDouble("TotalPagado"));
				}			
			} catch (SQLException e) {
				
				e.printStackTrace();
				System.out.println("Error al hacer la consulta: "+Select);
			}
    }
	public static double obtenerPrecioDiaVivienda(int codVivienda) {
	    String query = "SELECT precio_Dia FROM vivienda WHERE CodVivienda = ?";
	    double precioDia = 0.0;
	    
	    try {
	        PreparedStatement statement = ConectorBD.conexion.prepareStatement(query);
	        statement.setInt(1, codVivienda);
	        ResultSet rs = statement.executeQuery();
	        
	        if (rs.next()) {
	            precioDia = rs.getDouble("precio_Dia");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error al obtener el precio de la vivienda.");
	    }

	    return precioDia;
	}

}
