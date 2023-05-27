package jml.ot;

import java.util.Scanner;

import jml.ot.colors.AnsiColors;

public class Debug2 {
	
	public static void main(String[] args)
	{
		AnsiColors.cls();
		Scanner scanner = new Scanner(System.in);
		while(true)
		{
			System.out.print("input test");
			System.out.println(scanner.nextLine());
		}
	}

}
