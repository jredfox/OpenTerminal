package jml.ot;

import java.awt.Color;

import jml.ot.colors.AnsiColors;

public class Debug {

	public static void main(String[] args)
	{
		TerminalApp app = new TerminalApp("APP", "APP TITLE", "");
		app.pause = true;
		app.softPause = true;
		OpenTerminal.open(app);
		System.out.println(AnsiColors.INSTANCE.formatColor(null, Color.GREEN, "hello world"));
	}
	
}
