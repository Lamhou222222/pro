package view;

import java.util.Scanner;


import repositorios.GestionOficina;
import repositorios.GestionVivienda;



public class MenuOficina {
	 public static void mostrarMenuVivienda(Scanner sc) {
	        boolean salir = false;

	        while (!salir) {
	            System.out.println("\n--- Menú oficina ---");
	            System.out.println("1.- Buscar en Oficanas");
	            System.out.println("2.- Datos de oficina");
	            System.out.println("3.- Salir");
	            System.out.print("Selecciona una opción: ");

	            int opcion = sc.nextInt();
	            sc.nextLine(); // Limpiar el buffer

	            switch (opcion) {
	                case 1:
	                	MostrarViviendasOdicina(sc);
	                        break;
	    
	                case 2:
	                	GestionOficina.Datosoficina()
	                    break;
	                case 3: 
	                    System.out.println("Saliendo del menú. ¡Hasta luego!");
	                    break;
	                    
	                default:
	                    System.out.println("Opción no válida. Intenta de nuevo.");
	                    break;
	            }
	        }
	 }
	
	 public static void MostrarViviendasOdicina(Scanner sc) {
     	System.out.println("Inserta el numero de la oficina :");
	        boolean salir = false;

	        while (!salir) {
	            System.out.println("\n--- Menú Oficinas ---");
	            System.out.println("1.- Oficina Asia");
	            System.out.println("2.- Oficina America");
	            System.out.println("3.- Oficina Europa");
	            System.out.println("4.- Salir");
	            System.out.print("Selecciona una opción: ");

	            int opcion = sc.nextInt();
	            sc.nextLine(); // Limpiar el buffer
	            GestionOficina.mostrarOficinaViviendasBD(opcion);
	            switch (opcion) {
	                case 1:
	                
	                        break;
	    
	                case 2:
	                	
	                    break;
	                case 3:
	                   //modificarVivienda()
	                 break;
	              
	                case 4:
	                    salir = true;
	                    System.out.println("Saliendo del menú. ¡Hasta luego!");
	                    break;
	                default:
	                    System.out.println("Opción no válida. Intenta de nuevo.");
	                    break;
	            }
	           
	        }
	        
	       
	 }

}
