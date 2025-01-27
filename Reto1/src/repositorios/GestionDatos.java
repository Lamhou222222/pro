package repositorios;

import java.util.Scanner;

import clases.Usuario;

public class GestionDatos {

	public static Scanner sc=new Scanner(System.in);
	
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
	public static double modificarPrecioVivienda(double precio) {
		
		
		return precio;
		
	}
}
