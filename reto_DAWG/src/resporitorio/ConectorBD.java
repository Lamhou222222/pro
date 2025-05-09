package resporitorio;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectorBD {
	public static Connection conexion;
	   
    public static void conectar(){

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver cargado");        
            try{
      
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/AmigosPeludos","root","1DAW3_BBDD");
          
            System.out.println("Conexion establecida");
            System.out.println();
          
        }catch(Exception e){
            System.out.println("Error en la conexion");
        }
        }catch(Exception e){
            System.out.println("Error en el driver");
        }
    }
    
    public static void cerrarConexion() {
    	try {
			conexion.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static void getConexion() {
    	
    }
}
