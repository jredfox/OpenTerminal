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
			"/usr/bin/sakura",//TODO: fix this terminal
			"/usr/bin/Eterm",
			"/usr/bin/mate-terminal",
			"/usr/bin/xfce4-terminal",
			"/usr/bin/deepin-terminal",
			"/usr/bin/kitty",
			"/usr/bin/konsole",
			"/usr/bin/lilyterm",
			"/usr/bin/lxterminal",
			"/usr/bin/mlterm",
			"/usr/bin/pterm",
			"/usr/bin/qterminal",
			"/usr/bin/rxvt-unicode",
			"/usr/bin/stterm",
			"/usr/bin/terminator",
			"/usr/bin/termit",
			"/usr/bin/tilix",
			"/usr/bin/xiterm+thai",
			"/usr/bin/gcm-calibrate",//old start
			"/usr/bin/gnome-terminal",
			"/usr/bin/mosh-client",
			"/usr/bin/mosh-server",
			"/usr/bin/mrxvt",           
			"/usr/bin/mrxvt-full",        
			"/usr/bin/roxterm",          
			"/usr/bin/rxvt-unicode",        
			"/usr/bin/urxvt",           
			"/usr/bin/urxvtd",
			"/usr/bin/xfce4-terminal",   
			"/usr/bin/xterm",
			"/usr/bin/aterm",
			"/usr/bin/guake",//TODO fix not executing command
			"/usr/bin/Kuake",
			"/usr/bin/rxvt",
			"/usr/bin/rxvt-unicode",
			"/usr/bin/terminator",
			"/usr/bin/terminology", //TODO: see if there is a fix for this terminal
//			"/usr/bin/tilda", //TODO: -c is the execute flag yet it doesn't work as is
			"/usr/bin/wterm",
//			"/usr/bin/yakuake", //TODO: figure out why there is no execute flag
			"/usr/bin/Eterm",
			"/usr/bin/gnome-terminal.wrapper",
			"/usr/bin/koi8rxterm",
			"/usr/bin/konsole",
			"/usr/bin/lxterm",
			"/usr/bin/mlterm",
			"/usr/bin/mrxvt-full",
			"/usr/bin/roxterm",
			"/usr/bin/rxvt-xpm",
			"/usr/bin/rxvt-xterm",
			"/usr/bin/urxvt",
			"/usr/bin/uxterm",
			"/usr/bin/xfce4-terminal.wrapper",
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