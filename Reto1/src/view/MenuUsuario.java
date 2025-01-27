package view;

import java.util.Scanner;

import clases.Usuario;
import repositorios.GestionDatos;
import repositorios.GestionUsuario;

public class MenuUsuario {
	
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
           	GestionUsuario.loginUsuario(email, contraseña);
                break;      
    
            case 3: 
                System.out.println("Saliendo del menú. ¡Hasta luego!");
                break;
                
            default:
                System.out.println("Opción no válida. Intenta de nuevo.");
        }
    }

	}

}
