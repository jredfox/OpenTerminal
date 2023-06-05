package jml.ot;

import java.awt.Color;

import jml.ot.colors.AnsiColors;

public class Debug {

	public static void main(String[] args)
	{
		OpenTerminal.open(new TerminalApp("APP","",""));
		System.out.println(AnsiColors.INSTANCE.formatColor(null, Color.GREEN, "hello world"));
	}
	
}
