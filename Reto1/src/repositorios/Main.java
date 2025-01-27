package repositorios;

import java.util.Scanner;

import view.Menu;

public class Main {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);

		ConectorBD.conectar();
		
		Menu.mostrarMenuVivienda(sc);
		
		
		sc.close();
	}

}
