package view;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import clases.Reserva;
import clases.Vivienda;
import repositorios.GestionReserva;
import repositorios.GestionVivienda;



public class MenuReservas {
	
	public static void mostrarMenuReservas(Scanner sc) {
	    boolean salir = false;

	    while (!salir) {
	        System.out.println("\n--- Menú Reservas ---");
	        System.out.println("1.- Añadir Reserva");
	        System.out.println("2.- Mostrar Reserva");
	        System.out.println("3.- Modificar Reserva");
	        System.out.println("4.- Volver atras");
	        System.out.println("5.- Salir");
	        System.out.println();
	        System.out.print("Selecciona una opción: ");

	        int opcion = sc.nextInt();
	        sc.nextLine(); // Limpiar el buffer

	        switch (opcion) {
	            case 1:
	              Reserva res=agregarReserva(sc);
	              GestionReserva.insertarReserva(res);
	                    break;

	            case 2:
	            	GestionVivienda.mostrarViviendasBD();
	                break;
	            case 3:
	               //modificarVivienda()
	             
	            case 4:
	                //  buscarVivienda()
	                break;
	            case 5:
	            	return;
	            case 6:
	                salir = true;
	                System.out.println("Saliendo del menú. ¡Hasta luego!");
	                break;
	            default:
	                System.out.println("Opción no válida. Intenta de nuevo.");
	        }

	        }
	    }
	private static Date convertirFecha(String fechaString) {
	       
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy/MM/dd");
        java.sql.Date fechaSql = null;
        try {
            // Convertir el String a java.util.Date
            java.util.Date fechaUtil = formatoEntrada.parse(fechaString);
            
            // Convertir el java.util.Date a java.sql.Date
            fechaSql = new java.sql.Date(fechaUtil.getTime());
            
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fechaSql;
    }
	public static Reserva agregarReserva(Scanner sc) {
		
		System.out.println("\n--- Añadir Reserva ---");
		System.out.println("Fecha de Entrada:");
		String fechaE=sc.nextLine();
		Date fechaEd=MenuReservas.convertirFecha(fechaE);
		sc.nextLine();
        System.out.print("Fecha de Salida: ");
        String fechaS=sc.nextLine();
		Date fechaSd=MenuReservas.convertirFecha(fechaS);

		long ms= fechaEd.getTime()-fechaSd.getTime();
		long dias= ms/(1000*60*60*24);
        System.out.print("Número de huespedes: ");
        int huespedes = sc.nextInt();
        
        System.out.print("Total pagado: ");
        double totalPagado=0;
        //System.out.println(dias*);
        
        
        Reserva reserva= new Reserva(fechaE, fechaS, huespedes, totalPagado);
		return reserva;
		
	}
	
	 }


