package jml.ot;

public class OSUtil {
	
	private static String osName = System.getProperty("os.name").toLowerCase();
	private static boolean isWindows = osName.contains("windows");
	private static boolean isLinux = osName.contains("linux");
	private static boolean isMac = osName.contains("mac") || osName.contains("osx") && !isLinux;
	
	public static String[] windows_terminals = new String[]
	{
		"cmd",
		"powershell",//powershell doesn't work and has never worked. it can't execute batch files nor even basic commands or even handle spaces
		"wt"//Windows Terminal it's not a terminal itself but simply a UI. however it looks nicer then conhost.exe and can switch between terminals only wt-cmd is supported
	};
	
	public static String[] mac_terminals = new String[]
	{
		"/bin/bash"
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
			"/usr/bin/xvt",
	};
	
	public static final String[] crossplat_terminals = new String[]
	{
		"powershell"//Apparently the sh*t powershell is crossplatform now			
	};
	
	public static String getTerminal()
	{
		String[][] tlist = getTerminals();
		for(String[] cmds : tlist)
		for(String cmd : cmds)
		{
			try 
			{
				if(!cmd.equals("wt"))
					Runtime.getRuntime().exec(cmd + " " + getExeAndClose() + " cd " + System.getProperty("user.dir"));
				else
				{
					//TODO: figure out a better way then creating the UI to figure out if it exists
					ProcessBuilder pb = new ProcessBuilder(cmd);
					Process p = pb.start();
					p.destroy();
				}
				return cmd;
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

	public static String[][] getTerminals()
	{
		return new String[][]{getOsTerminal(), getCrossPlatTerminal()};
	}
	
	public static String[] getCrossPlatTerminal() 
	{
		return crossplat_terminals;
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

}