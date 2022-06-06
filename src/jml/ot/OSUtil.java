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
		"/bin/zsh",
		"/bin/sh",
		"/bin/csh", 
		"/bin/dash", 
		"/bin/ksh", 
		"/bin/tcsh"
	};
	
	public static String[] linux_terminals = new String[]
	{
		"alacritty",//ubuntu snap app
		"aterm",
		"cool-retro-term",
		"deepin-terminal",
		"Eterm",
		"foot",
		"gnome-terminal",
		"gnome-terminal.wrapper",
		"guake",
		"kitty",
		"kgx",
		"konsole",
		"lilyterm",
		"lxterm",
		"lxterminal",
		"mate-terminal",
		"mlterm",
		"mrxvt",
		"mrxvt-full",
		"pangoterm",
		"pterm",
		"qterminal",
		"roxterm",
		"rxvt",
		"rxvt-unicode",
		"rxvt-xpm",
		"rxvt-xterm",
		"sakura",
		"st",
		"stterm",
		"terminalpp",
		"terminus",
		"terminator",
		"terminology",
		"termit",
		"tilda",
		"tilix",
		"urxvt",
		"urxvtc",
		"urxvtcd",
		"uxterm",
		"vala-terminal",
		"wterm",
		"xfce4-terminal",
		"xfce4-terminal.wrapper",
		"xiterm+thai",
		"xterm",
		"xvt"
	};
	
	public static String getTerminal()
	{
		String[] cmds = getTerminals();
		for(String cmd : cmds)
		{
			if(findExe(cmd) != null)
			{
				return cmd;
			}
		}
		System.err.println("Unable to find terminal:" + System.getProperty("os.name") + " report to https://github.com/jredfox/OpenTerminal/issues");
		return null;
	}
	
	/**
	 * test if your terminal string is actually your terminal
	 */
	public static boolean isTerminalValid(String term) 
	{
		return findExe(term) != null;
	}
	
	public static String findExe(String name)
	{
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator)) 
	    {
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute())
	            return file.getAbsolutePath();
	    }
	    return null;
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
			case "gnome-terminal":
				return "--";//same as -x which doesn't support parameterized parsing of the command but updated to get around the depriated values
			case "gnome-terminal.wrapper":
				return x;
			case "xfce4-terminal":
				return x;
			case "terminator":
				return x;
			case "sakura":
				return x;
			case "mate-terminal":
				return x;
			case "tilda":
				return "-c";
		}
		return e;
	}

}