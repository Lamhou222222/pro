package Connectores;

import java.util.Scanner;

import clases.Usuario;

public class GestionDatos {

	public static Scanner sc=new Scanner(System.in);
	
	public static Usuario pedirDatosUsuario() {
		
		System.out.println("Nombre:");
		String nombre=sc.nextLine();
		System.out.println("Apellido:");
		String apellido=sc.nextLine();
		Usuario usuario=new Usuario();
		return usuario;
		
		
	}
//public static double pdirPrecioViviendoa() {
		
	
		
		
	}
	
	
//}
