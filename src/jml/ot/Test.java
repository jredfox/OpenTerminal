package jml.ot;

import java.awt.Color;
import java.io.IOException;

import jml.ot.colors.AnsiColors;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		long ms = System.currentTimeMillis();
		TerminalApp app = new TerminalApp("test", "Test App", "1.0.0", false) 
		{
			@Override
			public Profile getProfile() 
			{
				if(TerminalUtil.isWindows())
				{
					Profile p = new Profile(Color.CYAN, Color.WHITE);
					p.ansiFormat = AnsiColors.UNDERLINE;
					if(this.terminal.equals("powershell"))
					{
						p.wtScheme = "Campbell Powershell";
						p.wtTab = "42f5ec";
//						p.wtMaximized = true;
//						p.wtFullScreen = true;
					}
					return p;
//					return null;
				}
				else if(TerminalUtil.isMac())
				{
//					Profile p = Profile.newMac("jredfox.openterminal.blackglass", "resources/jml/ot/mac/BlGlass.terminal");
					Profile p = Profile.newMac("jredfox.openterminal.purpleCollege", "resources/jml/ot/mac/college.terminal");
//					Profile p = Profile.newMac("Red Sands");
//					Profile p  = new Profile();
					p.bg = Color.GRAY;
					p.fg = Color.GREEN;
					return p;
				}
				else if(TerminalUtil.isLinux())
				{
					return new Profile(Color.CYAN, Color.WHITE);
				}
				return null;
			}
		};
		if(TerminalUtil.isLinux())
		{
			app.terminal = "gnome-terminal";//set's the initial terminal the configuration overrides this from TerminalApp#getTerminalExe called by OpenTerminal#open
		}
//		app.pause = false;
		OpenTerminal.open(app);
		printTest(app.colors);
//		System.out.println("launch in:" + (System.currentTimeMillis() - ms) + "ms");
	}
	
	public static void printTest(AnsiColors colors)
	{
		System.out.println("clear me");
//		colors.setReset(Color.white, Color.CYAN, true);
//		colors.setReset(Color.BLUE, Color.BLACK, true);
		colors.println(Color.WHITE, Color.YELLOW, "SUP YELLOW!");
		colors.print(Color.BLACK, Color.GREEN, AnsiColors.OVERLINE + AnsiColors.UNDERLINE + AnsiColors.ITALIC + "Classic Green on Black");
		System.out.print("a");
		System.out.println("b");
	}
}
