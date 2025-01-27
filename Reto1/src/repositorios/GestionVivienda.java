package repositorios;


import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.util.Scanner;


public class GestionVivienda {
	public static  Connection conexion;
	
    private Scanner scanner = new Scanner(System.in);

    public static void insertarVivienda(Vivienda vivienda) {
        
            String query = "INSERT INTO vivienda (ciudad, direccion, descripcion, num_hab, precio_dia,Tipo_Vivienda,Dias,Semanas) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try ( 
                 PreparedStatement statement = conexion.prepareStatement(query)) {

               
                statement.setString(1, vivienda.g());
                statement.setString(2, direccion);
                statement.setString(3, descripcion);
                statement.setInt(4, numHab);
                statement.setDouble(5, precioDia);
                statement.setString(6,tipo_Vivienda);
                if(tipo_Vivienda.equals("Villa")) {
                	 statement.setInt(7,(Integer)null);
                	 statement.setInt(8,semanas);
                }
                else if(tipo_Vivienda.equals("Piso")) {
                	
                }

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("¡Vivienda añadida con éxito!");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Entrada inválida. Por favor, introduce valores numéricos donde corresponda.");
        } catch (SQLException e) {
            System.out.println("Error de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    //  eliminar una vivienda
    public static void eliminarVivienda(Scanner scanner) {
        System.out.println("\n--- Eliminar Vivienda ---");
        try {
            System.out.print("Introduce el código de la vivienda que deseas eliminar: ");
            int codVivienda = Integer.parseInt(scanner.nextLine());

            String query = "DELETE FROM viviendas WHERE id = ?";
            
            try ( 
                 PreparedStatement statement = conexion.prepareStatement(query)) {

                statement.setInt(1, codVivienda);

                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("¡Vivienda eliminada con éxito!");
                } else {
                    System.out.println("No se encontró ninguna vivienda con el código proporcionado.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Entrada inválida. Por favor, introduce un código numérico.");
        } catch (SQLException e) {
            System.out.println("Error de SQL: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

   

    
}

