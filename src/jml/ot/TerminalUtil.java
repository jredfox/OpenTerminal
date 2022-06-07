package jml.ot;

import java.io.File;
import java.util.List;

import jredfox.common.utils.JavaUtil;

public class TerminalUtil {
	
	private static String osName = System.getProperty("os.name").toLowerCase();
	private static boolean isWindows = osName.contains("windows");
	private static boolean isLinux = osName.contains("linux") || osName.contains("nux") || osName.contains("aix");
	private static boolean isMac = osName.contains("mac") && !isLinux || osName.contains("osx") || osName.contains("darwin");
	private static boolean isChrome = osName.contains("google") || osName.contains("chromeos") || osName.contains("pixelbook"); //TODO: see what the boolean is actually suppose to be

	public static String[] conHost = new String[]
	{
		"conhost",
		"wt"//windows terminal is a console host not a terminal
	};
	
	public static List<String> windows_terminals = JavaUtil.asArray(new String[]
	{
		"cmd",
		"powershell",//powershell doesn't work and has never worked. it can't execute batch files nor even basic commands nor even handle spaces
	});
	
	public static List<String> mac_terminals = JavaUtil.asArray(new String[]
	{
		"Terminal.app",
	});
	
	public static List<String> linux_terminals = JavaUtil.asArray(new String[]
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
	});
	
	public static String getTerminal()
	{
		List<String> terms = TerminalUtil.getOsTerminals();
		for(String cmd : terms)
		{
			if(findExe(cmd) != null)
			{
				return cmd;
			}
		}
		
		for(List<String> arr : TerminalUtil.getTerminals())
		{
			if(arr == terms)
				continue;
			for(String cmd : arr)
			{
				if(findExe(cmd) != null)
				{
					return cmd;
				}
			}
		}
		System.err.println("Unable to find terminal:" + System.getProperty("os.name") + " report to https://github.com/jredfox/OpenTerminal/issues");
		return null;
	}
	
	/**
	 * test if your terminal string is actually your terminal
	 */
	public static boolean isExeValid(String term) 
	{
		return findExe(term) != null;
	}
	
	public static String[] macAppPaths = new String[]
	{
		"/Applications",
		"/System/Applications/",
		"/System/Applications/Utilities/",
		"/System/Library/CoreServices/",
		"/System/Library/CoreServices/Applications"
	};
	
	public static String findExe(String name)
	{
		String ext = isWindows() ? ".exe" : isMac() ? ".app" : "";//TODO: test macOs and confirm functionality on windows
		String fname = name.contains(".") ? name : name + ext;
		boolean hasF = ext.isEmpty();
		
		//search the full path of the dir before searching env path
		if(name.contains("/"))
		{
			File path = new File(name);
			File fpath = new File(fname);
			if(path.canExecute())
				return path.getPath();
			else if(hasF && isExe(fpath))
				return fpath.getPath();
		}
		
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator)) 
	    {
	        File file = new File(dirname, name);
	        File ffile = new File(dirname, fname);
	        if (isExe(file))
	            return file.getPath();
	        else if(hasF && isExe(ffile))
	        	return ffile.getPath();
	    }
	    
	    //macOS start here
	    for(String root : macAppPaths)
	    {
	    	File app = new File(root, fname);
	    	if(isExe(app))
	    		return app.getPath();
	    }
	    
	    return null;
	}
	
	public static boolean isExe(File f)
	{
		return (f.isFile() || f.getName().endsWith(".app")) && f.canExecute();
	}

	@SuppressWarnings("unchecked")
	public static List<String>[] getTerminals()
	{
		return new List[] {windows_terminals, mac_terminals, linux_terminals};
	}
	
	public static List<String> getOsTerminals()
	{
		return isWindows() ? windows_terminals : isMac() ? mac_terminals : isLinux() ? linux_terminals : null;
	}

	/**
	 * runs the command in the background by default and closes
	 * NOTE: linux doesn't support run in the background and execute they are two seperate flags assume the background or hide flags exist
	 */
	public static String getExeAndClose()
	{
		return isWindows() ? "/c" : "-c";
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
	
	public static boolean isChromeOs()
	{
		return isChrome;//TODO: test and it's probably not working ;)
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

	public static String getLinuxExe(String term)
	{
		String e = "-e";
		String x = "-x";
		switch (term)
		{
			case "gnome-terminal":
				return "--";//same as -x which doesn't support parameterized parsing of the command but updated to get around the depreciated values
			case "gnome-terminal.wrapper":
				return "--";
			case "xfce4-terminal":
				return x;
			case "terminator":
				return x;
			case "mate-terminal":
				return x;
			case "tilda":
				return "-c";
		}
		return e;
	}

}