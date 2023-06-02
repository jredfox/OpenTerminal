package jml.ot;

import java.awt.Color;

import jml.ot.colors.AnsiColors;

public class Debug {

	public static void main(String[] args)
	{
		System.out.println(Color.cyan);
		System.out.println(AnsiColors.INSTANCE.pickerAnsi4Bit.pickColor(Color.CYAN));
	}
	
}
