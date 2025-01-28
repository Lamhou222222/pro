package main;

import java.util.Scanner;

import repositorios.ConectorBD;
import view.Menu;

public class Main {

	public static void main(String[] args) {
		Scanner sc=new Scanner(System.in);

		ConectorBD.conectar();
		
		
		
		
		sc.close();
	}

}
