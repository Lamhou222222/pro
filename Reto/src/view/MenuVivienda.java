package view;


import java.util.Scanner;
import clases.Vivienda;

import repositorios.GestionVivienda;

public class MenuVivienda {
	
	 public static void mostrarMenuVivienda(Scanner sc) {
	        boolean salir = false;

	        while (!salir) {
	            System.out.println("\n--- Menú Vivienda ---");
	            System.out.println("1.- Agregar Vivienda");
	            System.out.println("2.- Mostrar Viviendas");
	            System.out.println("3.- Modificar Vivienda");
	            System.out.println("4.- Buscar Vivienda");
	            System.out.println("5.- Volver atras");
	            System.out.println("6.- Salir");
	            System.out.println();
	            System.out.print("Selecciona una opción: ");

	            int opcion = sc.nextInt();
	            sc.nextLine(); // Limpiar el buffer

	            switch (opcion) {
	                case 1:
	                  Vivienda viv=agregarVivienda(sc);
	                  GestionVivienda.insertarVivienda(viv);
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
	        public static Vivienda agregarVivienda(Scanner sc) {
	    		System.out.println("\n--- Añadir Vivienda ---");
	    		System.out.println("Id Oficina:");
	    		int idOficina=sc.nextInt();
	    		sc.nextLine();
	            System.out.print("Ciudad: ");
	            String ciudad = sc.nextLine();
	            System.out.print("Dirección: ");
	            String direccion = sc.nextLine();
	            System.out.print("Descripción: ");
	            String descripcion = sc.nextLine();
	            System.out.print("Número de habitaciones: ");
	            int numHab = sc.nextInt();
	            sc.nextLine();
	            System.out.print("Precio por día: ");
	            double precioDia = sc.nextDouble();
	            sc.nextLine();
	            System.out.println("TipoVivienda (Villa/Piso):");
	            String tipo_Vivienda= sc.nextLine();
	            int dias =0;
	            int semanas =0;
	            do {
		            if(tipo_Vivienda.equalsIgnoreCase("Villa")) {
		            	System.out.println("Cuantas semanas :");
		            	semanas =sc.nextInt();
		            }
		            else if (tipo_Vivienda.equalsIgnoreCase("Piso")){
		            	System.out.println("Cuantos dias :");
		            	 dias =sc.nextInt();
		            
		            }else {
		            	System.out.println("Error. Introduce Villa o Piso:");
		            }
	            }while(!tipo_Vivienda.equalsIgnoreCase("Villa") && !tipo_Vivienda.equalsIgnoreCase("Piso"));
	           
	           Vivienda vivienda= new Vivienda(idOficina, ciudad, direccion, numHab, descripcion, precioDia, tipo_Vivienda, dias, semanas);

	            return vivienda;
	        }
	                 
	       
	 }

