package repositorios;

import java.util.Scanner;

import clases.Usuario;
import clases.Vivienda;

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
	public static Vivienda agregarVivienda() {
		System.out.println("\n--- Añadir Vivienda ---");
        System.out.print("Ciudad: ");
        String ciudad = sc.nextLine();
        System.out.print("Dirección: ");
        String direccion = sc.nextLine();
        System.out.print("Descripción: ");
        String descripcion = sc.nextLine();
        System.out.print("Número de habitaciones: ");
        int numHab = sc.nextInt();
        System.out.print("Precio por día: ");
        double precioDia = sc.nextDouble();
        System.out.println("TipoVivienda (Villa/Piso):");
        String tipo_Vivienda= sc.nextLine();
        int dias =0;
        int semanas =0;
        if(tipo_Vivienda.equals("Villa")) {
        	System.out.println("Cuantas dias :");
        	dias =sc.nextInt();
        }
        else if (tipo_Vivienda.equals("Piso")){
        	System.out.println("Cuantas semanas :");
        	 semanas =sc.nextInt();
        }
        Vivienda vivienda =new Vivienda (ciudad, direccion, numHab, descripcion, precioDia, tipo_Vivienda, dias, semanas);
        return vivienda;
	}

	public static double modificarPrecioVivienda(double precio) {
		
		
		return precio;
		
	}
}
