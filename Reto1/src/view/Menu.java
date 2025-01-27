package view;

import java.sql.SQLException;
import java.util.Scanner;

import clases.Usuario;
import repositorios.GestionDatos;
import repositorios.GestionUsuario;
import repositorios.GestionVivienda;
public class Menu {
	
	
	public static void mostrarMenuUsuario(Scanner sc) {
		int opcion = 0;
		while (opcion!=3) {
		System.out.println("\n--- Menú Usuario ---");
        System.out.println("1.- Crear Usuario");
        System.out.println("2.- Login Usuario");
        System.out.println("3.- Salir");
        System.out.print("Selecciona una opción: ");
        opcion = sc.nextInt();
        sc.nextLine(); // Limpiar el buffer

        switch (opcion) {
            case 1:
            	   Usuario us=GestionDatos.crearUsuario();
            	   	GestionUsuario.insertarUsuario(us);
                    break;

            case 2: 	
           //	GestionUsuario.loginUsuario();
                break;      
    
            case 3: 
                System.out.println("Saliendo del menú. ¡Hasta luego!");
                break;
                
            default:
                System.out.println("Opción no válida. Intenta de nuevo.");
        }
    }

	}

	 public static void mostrarMenuVivienda(Scanner sc) {
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
