package view;

import java.util.Scanner;

import clases.Usuario;

import repositorios.GestionUsuario;

public class MenuUsuario {
	public static Scanner sc=new Scanner(System.in);
	
	public static void mostrarMenuUsuario(Scanner sc) {
		int opcion = 0;
		while (opcion!=3) {
		System.out.println("\n--- Menú Usuario ---");
        System.out.println("1.- Crear Usuario");
        System.out.println("2.- Login Usuario");
        System.out.println("3.- Salir");
        System.out.print("Selecciona una opción: ");
        opcion = sc.nextInt();
        sc.nextLine(); // Limpia el buffer

        switch (opcion) {
            case 1:
            	Usuario us=crearUsuario();
            	GestionUsuario.insertarUsuario(us);
                break;
                
            case 2: 
            	System.out.println("Ingresa tu mail:");
            	String email=sc.nextLine();
            	System.out.println("Ingresa tu contraseña:");
            	String contraseña=sc.nextLine();
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
	public static Usuario crearUsuario() {
			
			System.out.println("Ingresa tu DNI:");
			String dni=sc.nextLine();
			System.out.println("Ingresa tu nombre:");
			String nombre=sc.nextLine();
			System.out.println("Ingresa tu apellido:");
			String apellido=sc.nextLine();
			System.out.println("Ingresa tu nombre de usuario:");
			String nomUs=sc.nextLine();
			System.out.println("Ingresa tu email:");
			String email=sc.nextLine();
			System.out.println("Ingresa tu contraseña:");
			String contraseña=sc.nextLine();
			String rol;
			if(email.equals("ikdgg@plaiaundi.net") || email.equals("ikdgs@plaiaundi.net")) {
				rol="Admin";
			}else {
				rol="Usuario";
			}
			
			Usuario usuario=new Usuario(dni, nombre, apellido, nomUs, email, contraseña, rol );
			return usuario;	

	}
	public static void menuLogin() {
		
	}
	

}
