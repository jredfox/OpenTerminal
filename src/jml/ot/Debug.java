package jml.ot;

import java.awt.Color;

import jml.ot.TerminalApp.Profile;
import jml.ot.colors.AnsiColors;

public class Debug {

	public static void main(String[] args)
	{
		TerminalApp app = new TerminalApp("APP", "", "");
		Profile p = new Profile();
		app.setPauseMsg(Color.CYAN, Color.GREEN, "Exiting...", p);
//		app.profile = p;
		OpenTerminal.open(app);
		System.out.println(AnsiColors.INSTANCE.formatColor(null, Color.GREEN, "hello world"));
	}
	
}
