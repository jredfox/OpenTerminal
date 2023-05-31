package jml.ot;

import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import jml.ot.colors.AnsiColors;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException
	{
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
					//colored pause message
					p.pauseMsg = AnsiColors.INSTANCE.formatColor(Color.GREEN, Color.WHITE, "PROGRAM is Exiting:", false);
					return p;
//					return null;
				}
				else if(TerminalUtil.isMac())
				{
//					Profile p = Profile.newMac("jredfox.openterminal.blackglass", "resources/jml/ot/mac/BlGlass.terminal");
					Profile p = Profile.newMac("jredfox.openterminal.purpleCollege", "resources/jml/ot/mac/college.terminal");
//					Profile p = Profile.newMac("Red Sands");
//					Profile p  = new Profile();
//					p.bg = Color.GRAY;
//					p.fg = Color.GREEN;
//					p.pauseMsg = "TEST";
//					p.pauseMsg = AnsiColors.INSTANCE.formatColor(Color.GREEN, Color.WHITE, "PROGRAM is Exiting:", false);
					return p;
				}
				else if(TerminalUtil.isLinux())
				{
					return new Profile(Color.CYAN, Color.WHITE);
				}
				return null;
			}
		};
		app.appClass = app.getClass();
		
		if(TerminalUtil.isLinux())
		{
			app.terminal = "gnome-terminal";//set's the initial terminal the configuration overrides this from TerminalApp#getTerminalExe called by OpenTerminal#open
		}
//		app.pause = false;
//		Profile p = app.getProfile();
//		app.shouldLog = true;
//		app.pause = false;
		OpenTerminal.open(app);
		
//		System.out.println("Starting CLI:" + app.appClass);
		
		//begin testing
		System.out.println("COLORMODE:" + app.colors.colorMode);
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("TEST:");
		System.out.println("TEST IN WAS:" + scanner.nextLine());
		System.out.println("2d line TEST WAS:" + scanner.nextLine());
		
//		BufferedReader bf = IOUtils.getReader(System.in);
//		System.out.print("BF1:");
//		System.out.println(bf.readLine());
//		System.out.print("BF2:");
//		System.out.println(bf.readLine());
		
//		app.colors.colors = app.colors.formatColor(p.bg, p.fg, p.ansiFormat, false);
//		app.colors.print(Color.BLACK, Color.green, "OLDE TESTE");
		System.out.println(app.colors.formatColor(Color.RED, Color.WHITE, "server to client test...", true));
//		System.out.println(app.colors.colors.replace(AnsiColors.ESC, "ESC"));
		app.manager.isRunning = false;//TODO: remove once PID auto detection has been reached
	}
	
//	public static void printTest(AnsiColors colors)
//	{
////		colors.setReset(Color.white, Color.CYAN, true);
////		colors.setReset(Color.BLUE, Color.BLACK, true);
//		colors.println(Color.WHITE, Color.YELLOW, "SUP YELLOW!");
//		colors.print(Color.BLACK, Color.GREEN, AnsiColors.OVERLINE + AnsiColors.UNDERLINE + AnsiColors.ITALIC + "Classic Green on Black");
//		System.out.print("a");
//		System.out.println("b");
//		System.out.println("colorMode:" + colors.colorMode + " colorFormat:" + colors.colors);
//	}
}
