package Connectores;

import java.sql.SQLException;
import java.util.Scanner;

public class GestionVivienda {
 
	 private Scanner scanner= new Scanner(System.in);
	 
	 public  void agregarViviendas() throws SQLException {
		    System.out.println("\n--- Añadir Vivienda ---");
		    System.out.print("Ciudad : ");
		    String titulo = scanner.nextLine();
		    System.out.print("Direccion: ");
		    String director = scanner.nextLine();
		    System.out.print("Descripcion : ");
		    String descripcion = scanner.nextLine();
		    System.out.print("NumHab: ");
		    int numHab = Integer.parseInt(scanner.nextLine());
		    System.out.print("Precio/Dia: ");
		    double precioDia = scanner.nextDouble();
}
}
