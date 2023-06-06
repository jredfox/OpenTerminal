package jml.ot;

import java.awt.Color;

import jml.ot.colors.AnsiColors;
import jredfox.common.utils.JREUtil;

public class Debug {

	public static void main(String[] args)
	{
		TerminalApp app = new TerminalApp("APP", "APP TITLE", "");
		app.pause = true;
		app.softPause = false;
		OpenTerminal.open(app);
		System.out.println(AnsiColors.INSTANCE.formatColor(null, Color.GREEN, "hello world"));
		while(true)
		{
			System.out.println(System.currentTimeMillis());
			JREUtil.sleep(1000);
		}
	}
	
}
