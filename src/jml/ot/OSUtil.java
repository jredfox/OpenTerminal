package jml.ot;

import java.io.File;

public class OSUtil {
	
	private static String osName = System.getProperty("os.name").toLowerCase();
	private static boolean isWindows = osName.contains("windows");
	private static boolean isLinux = osName.contains("linux");
	private static boolean isMac = osName.contains("mac") || osName.contains("osx") && !isLinux;
	
	public static String[] conHost = new String[]
	{
		"conhost",
		"wt"//windows terminal is a console host not a terminal
	};
	
	public static String[] windows_terminals = new String[]
	{
		"cmd",
		"powershell",//powershell doesn't work and has never worked. it can't execute batch files nor even basic commands or even handle spaces
	};
	
	public static String[] mac_terminals = new String[]
	{
		"/bin/bash",
		"/bin/sh",
		"/bin/zsh",
		"/bin/csh", 
		"/bin/dash", 
		"/bin/ksh", 
		"/bin/tcsh"
	};
	
	public static String[] linux_terminals = new String[]
	{
		"/usr/bin/aterm",
		"/usr/bin/deepin-terminal",
		"/usr/bin/Eterm",
		"/usr/bin/gnome-terminal",
		"/usr/bin/gnome-terminal.wrapper",
		"/usr/bin/guake",
		"/usr/bin/kitty",
		"/usr/bin/koi8rxterm",
		"/usr/bin/konsole",
		"/usr/bin/lilyterm",
		"/usr/bin/lxterm",
		"/usr/bin/lxterminal",
		"/usr/bin/mate-terminal",
		"/usr/bin/mlterm",
		"/usr/bin/mrxvt", //current index
		"/usr/bin/mrxvt-full",
		"/usr/bin/pterm",
		"/usr/bin/qterminal",
		"/usr/bin/roxterm",
		"/usr/bin/rxvt",
		"/usr/bin/rxvt-unicode",
		"/usr/bin/rxvt-xpm",
		"/usr/bin/rxvt-xterm",
		"/usr/bin/sakura",
		"/usr/bin/stterm",
		"/usr/bin/terminator",
		"/usr/bin/terminology",
		"/usr/bin/termit",
		"/usr/bin/tilix",
		"/usr/bin/urxvt",
		"/usr/bin/urxvtd",
		"/usr/bin/uxterm",
		"/usr/bin/wterm",
		"/usr/bin/xfce4-terminal",
		"/usr/bin/xfce4-terminal.wrapper",
		"/usr/bin/xiterm+thai",
		"/usr/bin/xterm",
		"/usr/bin/xvt"
	};	
	public static String getTerminal()
	{
		String[] cmds = getTerminals();
		for(String cmd : cmds)
		{
			try 
			{
				Runtime.getRuntime().exec(cmd + " " + getExeAndClose() + " cd " + System.getProperty("user.dir"));
			}
			catch (Throwable e) {}
		}
		System.err.println("Unable to find Os terminal for:" + System.getProperty("os.name") + " report to https://github.com/jredfox/OpenTerminal/issues");
		return null;
	}
	
	/**
	 * test if your terminal string is actually your terminal
	 */
	public static boolean isTerminalValid(String term) 
	{
		try 
		{
			Runtime.getRuntime().exec(term + " " + getExeAndClose() + " cd " + System.getProperty("user.dir"));
			return true;
		} 
		catch (Throwable e) {}
		return false;
	}

	public static String[] getTerminals()
	{
		return getOsTerminal();
	}

	public static String[] getOsTerminal()
	{
		return isWindows() ? windows_terminals : isMac() ? mac_terminals : isLinux() ? linux_terminals : null;
	}

	/**
	 * runs the command in the background by default and closes
	 */
	public static String getExeAndClose()
	{
		return isWindows() ? "/c" : (isMac() || isLinux()) ?  "-c" : null;
	}
	
	/**
	 * returns the linux execute in new window flag
	 */
	public static String getLinuxNewWin()
	{
		return "-x";
	}
	
	/**
	 * @return the terminal's quote
	 */
	public static String getQuote() 
	{
		return "\"";
	}

	/**
	 * @return the escape sequence to preserve characters
	 */
	public static String getEsc() 
	{
		return "\\\"";
	}
	
	public static boolean isWindows()
	{
		return isWindows;
	}
	
	public static boolean isMac()
	{
		return isMac;
	}
	
	public static boolean isLinux()
	{
		return isLinux;
	}
	
	public static boolean isUnsupported()
	{
		return !isWindows() && !isMac() && !isLinux();
	}
	
	public static File getAppData()
	{
		if(isWindows())
			return new File(System.getenv("APPDATA"));
	    String path = System.getProperty("user.home");
	    if(isMac())
	    	path += "/Library/Application Support";
	    return new File(path);
	}

	public static String getLinuxNewWin(String term) 
	{
		String e = "-e";
		String x = "-x";
		switch (term)
		{
			case "/usr/bin/gnome-terminal":
				return x;
			case "gnome-terminal.wrapper":
				return x;
			case "/usr/bin/xfce4-terminal":
				return x;
			case "/usr/bin/terminator":
				return x;
			case "/usr/bin/sakura":
				return x;
			case "/usr/bin/mate-terminal":
				return x;
			case "/usr/bin/tilda":
				return "-c";
		}
		return e;
	}

}