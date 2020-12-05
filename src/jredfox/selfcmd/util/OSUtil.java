package jredfox.selfcmd.util;

import java.io.File;

import jredfox.selfcmd.thread.ShutdownThread;

public class OSUtil {
	
	private static String osName = System.getProperty("os.name").toLowerCase();
	private static boolean isWindows = osName.contains("windows");
	private static boolean isMac = osName.contains("mac");
	private static boolean isLinux = osName.contains("linux");
	
	public static String[] windows_terminals = new String[]
	{
		"cmd",
		"powershell",//seems to freak out and seems to be beta even in 2020 with all it's bugs
	};
	
	public static String[] mac_terminals = new String[]
	{
		"bin/bash"	
	};
	
	public static String[] linux_terminals = new String[]
	{
			"/usr/bin/gcm-calibrate",
			"/usr/bin/gnome-terminal",
			"/usr/bin/mosh-client",
			"/usr/bin/mosh-server",
			"/usr/bin/mrxvt",           
			"/usr/bin/mrxvt-full",        
			"/usr/bin/roxterm",          
			"/usr/bin/rxvt-unicode",        
			"/usr/bin/urxvt",             
			"/usr/bin/urxvtd",
			"/usr/bin/vinagre",
			"/usr/bin/x-terminal-emulator",
			"/usr/bin/xfce4-terminal",   
			"/usr/bin/xterm",
			"/usr/bin/aterm",
			"/usr/bin/guake",
			"/usr/bin/Kuake",
			"/usr/bin/rxvt",
			"/usr/bin/rxvt-unicode",
			"/usr/bin/Terminator",
			"/usr/bin/Terminology",
			"/usr/bin/tilda",
			"/usr/bin/wterm",
			"/usr/bin/Yakuake",
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
				return cmd;
			} 
			catch (Throwable e) {}
		}
		System.err.println("Unable to find Os terminal for:" + System.getProperty("os.name") + " report to https://github.com/jredfox/SelfCommandPrompt/issues");
		return null;
	}

	public static String[] getTerminals()
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
	
	public static File getAppData()
	{
		if(isWindows())
		{
			return new File(System.getenv("APPDATA"));
		}
	    String path = System.getProperty("user.home");
	    if(isMac())
	    	path += "/Library/Application Support";
	    return new File(path);
	}
	
	/**
	 * NOTE: this isn't a shutdown event to prevent shutdown only a hook into the shutdown events. 
	 * That would be app specific this is jvm program (non app) specific which works for both
	 */
	public static void addShutdownThread(ShutdownThread sht)
	{
		throw new RuntimeException("Unsupported Check back in a future version!");
	}

	public static boolean isUnsupported()
	{
		return !isWindows() && !isMac() && !isLinux();
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

}
