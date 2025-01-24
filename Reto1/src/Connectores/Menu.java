package Connectores;

import java.sql.SQLException;
import java.util.Scanner;
public class Menu {
	

	 public static void mostrarMenu(Scanner sc) {
	        boolean salir = false;

	        while (!salir) {
	            System.out.println("\n--- Menú Vivienda ---");
	            System.out.println("1.- Agregar Vivienda");
	            System.out.println("2.- Eliminar Vivienda");
	            System.out.println("3.- Modificar Vivienda");
	            System.out.println("4.- Buscar Vivienda");
	            System.out.println("5.- Salir");
	            System.out.print("Selecciona una opción: ");

	            int opcion = sc.nextInt();
	            sc.nextLine(); // Limpiar el buffer

	            switch (opcion) {
	                case 1:
	                    
	                  GestionVivienda.agregarVivienda(sc);
	                        break;
	    
	                case 2:
	                	
	               	GestionVivienda.eliminarVivienda(sc);
	                    break;
	                    //  modificarVivienda()
	                    break;
	                case 4:
	                    //  buscarVivienda()
	                    break;
	                case 5:
	                    salir = true;
	                    System.out.println("Saliendo del menú. ¡Hasta luego!");
	                    break;
	                default:
	                    System.out.println("Opción no válida. Intenta de nuevo.");
	            }
	        }
	 }
}
