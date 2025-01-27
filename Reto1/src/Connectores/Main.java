package Connectores;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);

		ConectorBD.conectar();
		
		Menu.mostrarMenuVivienda(sc);
		
		
		sc.close();
	}

}
