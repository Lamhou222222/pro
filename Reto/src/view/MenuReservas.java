package view;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import clases.Reserva;
import repositorios.GestionOficina;
import repositorios.GestionReserva;
import repositorios.GestionUsuario;

public class MenuReservas {
	
	public static void mostrarMenuReservas(Scanner sc, int idOficina) {
	    boolean salir = false;

	    while (!salir) {
	        System.out.println("\n--- Menú Reservas ---");
	        System.out.println("1.- Disponibilidad fechas");
	        System.out.println("2.- Volver atras");
	        System.out.println("3.- Salir");
	        System.out.println();
	        System.out.print("Selecciona una opción: ");

	        int opcion2 = sc.nextInt();
	        sc.nextLine(); // Limpiar el buffer

	        switch (opcion2) {
	            case 1:   
	            	Reserva reserva=consultarFechas(sc);
	            	GestionReserva.consultarFechaBD(idOficina,reserva.getFechaEntrada(), reserva.getFechaSalida());
	            	Reserva res=agregarReserva(sc, reserva.getFechaEntrada(), reserva.getFechaSalida());
	            	GestionReserva.insertarReserva(res);
	                    break;

	            case 2:
	          
	             return;

	            case 3:
	                salir = true;
	                System.out.println("Finalizando programa ¡Hasta la próxima!");
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
	public static Reserva consultarFechas(Scanner sc) {
		System.out.println("Fecha de Entrada(yyyy/mm/dd):");
	    String fechaE = sc.nextLine();
	    Date fechaEd = MenuReservas.convertirFecha(fechaE);

	    System.out.print("Fecha de Salida(yyyy/mm/dd): ");
	    String fechaS = sc.nextLine();
	   
	    Date fechaSd = MenuReservas.convertirFecha(fechaS);
	    Reserva reserva=new Reserva();
	    reserva.setFechaEntrada(fechaEd);
	    reserva.setFechaSalida(fechaSd);
		return reserva;
	}
	public static Reserva agregarReserva(Scanner sc, Date fechaEd, Date fechaSd) {
	    System.out.println("\n--- Añadir Reserva ---");
	    System.out.println("Código de la vivienda:");
	    int codVivienda = sc.nextInt();
	    sc.nextLine();

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


