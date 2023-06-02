package jml.ot;

import java.awt.Color;
import java.io.IOException;
import java.util.Scanner;

import jml.ot.colors.AnsiColors;
import jml.ot.colors.AnsiColors.TermColors;
import jml.ot.colors.ColoredPrintStream;

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
//					p.pauseMsg = AnsiColors.INSTANCE.formatColor(Color.GREEN, Color.WHITE, "PROGRAM is Exiting:", false);
					this.setPauseMsg(Color.ORANGE, Color.WHITE, "Done", p);
					
					//start colored error test
					p.hasColoredErr = true;
					p.bgErr = Color.WHITE;
					p.fgErr = Color.RED;
//					p.ansiFormatErr = AnsiColors.UNDERLINE;
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
//					p.pauseMsg = "TEST";
//					p.pauseMsg = AnsiColors.INSTANCE.formsatColor(Color.GREEN, Color.WHITE, "PROGRAM is Exiting:", false);
//					p.setPauseMsg("E");
//					p.setPauseMsg(this.formatPauseColor(Color.BLUE, Color.BLACK, "Exiting"), this.formatPauseLowResColor(Color.BLUE, Color.BLACK, "Exiting"));
					this.setPauseMsg(Color.yellow, Color.green, "Exiting the savanah", p);
					return p;
				}
				else if(TerminalUtil.isLinux())
				{
					Profile p = new Profile(Color.CYAN, Color.WHITE);
//					p.setPauseMsg("Program is exiting...");
					String phd = AnsiColors.INSTANCE.formatColor(TermColors.TRUE_COLOR, Color.WHITE, Color.BLACK, "Program is Done Executing", false);
					String plr = AnsiColors.INSTANCE.formatColor(TermColors.XTERM_256, Color.WHITE, Color.BLACK, "Program is Done Executing", false);
					p.setPauseMsg(phd, plr);
					return p;
				}
				return null;
			}
		};
		app.appClass = app.getClass();
		
//		app.pause = false;
//		Profile p = app.getProfile();
//		app.shouldLog = true;
//		app.pause = false;
		OpenTerminal.open(app);
		app.colors.println(null, Color.BLACK, "BLACK");
		app.colors.println(null, Color.RED, "RED");
		app.colors.println(null, Color.GREEN, "Green");
		app.colors.println(null, Color.YELLOW, "Yellow");
		app.colors.println(null, Color.MAGENTA, "Magenta");
		app.colors.println(null, Color.CYAN, "Cyan");
		app.colors.println(null, Color.WHITE, "White");
		app.colors.println(null, Color.GRAY, "GREY");
		app.colors.println(null, new Color(231, 70, 80), "Bright Red");
		app.colors.println(null, new Color(22,198,12), "Bright Green");
		app.colors.println(null, new Color(249,241,165), "Bright Yellow");
		app.colors.println(null, new Color(59,120,255), "Bright Blue");
		app.colors.println(null, new Color(180,0,158), "Bright Magenta");
		app.colors.println(null, new Color(97,214,214), "Bright Cyan");
		app.colors.println(null, new Color(242,242,242), "Bright White");
//		((ColoredPrintStream)System.err).override = true;
//		System.out.println("hello");
//		System.err.println("TESTING ERR");
//		System.err.printf("Hello %s!", "World");
//		System.err.append(null, 0, 1);
//		System.out.println("goodby");
//		System.out.println("COLORMODE:" + app.colors.colorMode);
//		System.exit(0);
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
