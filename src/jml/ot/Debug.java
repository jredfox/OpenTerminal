package jml.ot;

import java.awt.Color;

import jml.ot.colors.AnsiColors;
import jml.ot.colors.AnsiColors.TermColors;

public class Debug {

	public static void main(String[] args)
	{
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.ANSI4BIT, Color.BLACK, Color.WHITE, "", false));
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.ANSI4BIT, AnsiColors.COLOR_DEFAULT, AnsiColors.COLOR_DEFAULT, "", false));
		System.out.println();
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.XTERM_256, Color.BLACK, Color.WHITE, "", false));
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.XTERM_256, AnsiColors.COLOR_DEFAULT, Color.WHITE, "", false));
		System.out.println();
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.TRUE_COLOR, Color.BLACK, Color.WHITE, "", false));
		System.out.println(AnsiColors.INSTANCE.formatColor(TermColors.TRUE_COLOR, AnsiColors.COLOR_DEFAULT, Color.WHITE, "", false));

	}
	
}
