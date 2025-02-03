package view;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import clases.Reserva;
import repositorios.GestionReserva;
import repositorios.GestionUsuario;

public class MenuReservas {
	
	public static void mostrarMenuReservas(Scanner sc) {
	    boolean salir = false;

	    while (!salir) {
	        System.out.println("\n--- Menú Reservas ---");
	        System.out.println("1.- Añadir Reserva");
	        System.out.println("2.- Volver atras");
	        System.out.println("3.- Salir");
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
	          
	             return;

	            case 3:
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
	    System.out.println("Código de la vivienda:");
	    int codVivienda = sc.nextInt();
	    sc.nextLine();

	    System.out.println("Fecha de Entrada(yyyy/mm/dd):");
	    String fechaE = sc.nextLine();
	    Date fechaEd = MenuReservas.convertirFecha(fechaE);

	    System.out.print("Fecha de Salida(yyyy/mm/dd): ");
	    String fechaS = sc.nextLine();
	    Date fechaSd = MenuReservas.convertirFecha(fechaS);

	    long ms = fechaSd.getTime() - fechaEd.getTime();
	    long dias = ms / (1000 * 60 * 60 * 24);

	    System.out.print("Número de huéspedes: ");
	    int huespedes = sc.nextInt();

	    double precioDia = GestionReserva.obtenerPrecioDiaVivienda(codVivienda);

	    double totalPagado = totalPagar(dias, precioDia);

	    String dniUsuario = GestionUsuario.getDniUsuario();
	    
	    Reserva reserva = new Reserva(dniUsuario, codVivienda, fechaEd, fechaSd, huespedes, totalPagado);
	    
	    return reserva;
	}
	public static Double totalPagar(long dias, double precioDia) {
		return dias*precioDia;
	}
	
	
	 }


