package view;



import java.util.ArrayList;
import java.util.Scanner;
import clases.Vivienda;
import repositorios.GestionVivienda;

public class MenuVivienda {
	
	 public static void mostrarMenuVivienda(Scanner sc) {
	        boolean salir = false;
	        
	        ArrayList <Vivienda> vivienda= new ArrayList<>();
	        
	        while (!salir) {
	            System.out.println("\n--- Menú Vivienda ---");
	            System.out.println("1.- Agregar Vivienda");
	            System.out.println("2.- Mostrar Viviendas");
	            System.out.println("3.- Modificar Vivienda");
	            System.out.println("4.- Volver atras");
	            System.out.println("5.- Salir");
	            System.out.println();
	            System.out.print("Selecciona una opción: ");

	            int opcion = sc.nextInt();
	            sc.nextLine(); // Limpiar el buffer

	            switch (opcion) {
	                case 1:
	                  Vivienda viv=agregarVivienda(sc);
	                  vivienda.add(viv);
	                  GestionVivienda.insertarVivienda(viv);
	                        break;
	    
	                case 2:
	                	GestionVivienda.mostrarViviendasBD();
	                    break;
	                case 3:
	                   modificarVivienda(vivienda, sc);
	                 
	                case 4:
	                	return;
	                case 5:
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
	            String tipo_Vivienda;
	            int dias;
	            int semanas;
	            do {
	            System.out.println("TipoVivienda (Villa/Piso):");
	            tipo_Vivienda= sc.nextLine();
	            dias =0;
	            semanas =0; 
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
	        private static void modificarVivienda(ArrayList<Vivienda> vivienda, Scanner sc) {
	    	    System.out.println("Introduce el Codigo de la vivienda a actualizar:");
	    	    int codV = sc.nextInt();
	    	    Vivienda viv = new Vivienda();
	    	    viv.setCodViv(codV);
	    	    
	    	    if (vivienda.contains(viv)) {
	    	    	System.out.println("Introduce la nueva oficina:");
	    	    	int idOf=sc.nextInt();
	    	    	viv.setIdOficina(idOf);
	    	        System.out.println("Introduce la nueva ciudad:");
	    	        String ciudad = sc.nextLine();
	    	        viv.setCiudad(ciudad);
	    	        System.out.println("Introduce la nueva dirección:");
	    	        String direc = sc.nextLine();
	    	        viv.setDireccion(direc);
	    	        System.out.println("Introduce el nuevo número de habitaciones:");
	    	        int numH = sc.nextInt();
	    	        viv.setNumHab(numH);
	    	        sc.nextLine();
	    	        System.out.println("Cambia la descripción de la vivienda:");
	    	        String desc=sc.nextLine();
	    	        viv.setDescripcion(desc);
	    	        System.out.println("Introduce el nuevo precio por dia:");
	    	        double precioD=sc.nextDouble();
	    	        viv.setPrecioDia(precioD);
	    	        System.out.println("¿Que tipo de vivienda es? (Villa/Piso):");
	    	        String tipoV=sc.nextLine();
	    	        viv.setTipo_Vivienda(tipoV);
	    	        System.out.println("Estancia en dias (Si es piso):");
	    	        int dias=sc.nextInt();
	    	        viv.setDias(dias);
	    	        System.out.println("Estancia en semanas (Si es villa):");
	    	        int semanas=sc.nextInt();
	    	        viv.setSemanas(semanas);
	    	        int index = vivienda.indexOf(viv);
	    	        vivienda.set(index, viv);
	    	        
	    	        
	    	        System.out.println("Vivienda actualizada.");
	    	    } else {
	    	        System.out.println("La vivienda no se encuentra en la lista.");
	    	    }
	    	}
	                 
	       
	 }

