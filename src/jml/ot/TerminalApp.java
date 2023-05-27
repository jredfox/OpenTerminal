package jml.ot;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jml.ipc.pipes.WrappedPrintStream;
import jml.ot.colors.AnsiColors;
import jml.ot.colors.AnsiColors.TermColors;
import jml.ot.terminal.BatchExe;
import jml.ot.terminal.GuakeTerminalExe;
import jml.ot.terminal.LinuxBashExe;
import jml.ot.terminal.LinuxCmdTerminalExe;
import jml.ot.terminal.MacBashExe;
import jml.ot.terminal.PowerShellExe;
import jml.ot.terminal.TerminalExe;
import jml.ot.terminal.TildaTerminalExe;
import jml.ot.terminal.ValaTerminalExe;
import jml.ot.terminal.host.ConsoleHost;
import jml.ot.terminal.host.WTHost;
import jredfox.common.config.MapConfig;
import jredfox.common.utils.FileUtil;

public class TerminalApp {
	
	public String id;
	public String name;
	public String version;
	public boolean force;//when enabled will always open a window
	public boolean pause;
	public String terminal = "";
	public String conHost = "";
	public List<String> linuxCmdsExe = new ArrayList<>(0);//configurable list to use LinuxCmdExe instead of LinuxBash or another
	public Map<String, String> linuxFlags = new HashMap<>(0);//override linux new window flags in case you have an updated or outdated version then is currently supported
	/**
	 * changing this to true will make you use 4 bit ANSI colors instead of xterm256 or true colors
	 */
	public boolean ANSI4BIT;
	public AnsiColors colors = new AnsiColors();
	/**
	 * when enabled it loggs boot errors and sanity reasons. The boot log only loogs the boot debug and not your program
	 */
	public boolean canLogBoot = true;
	/**
	 * Logger during boot of {@link OpenTerminal#open(TerminalApp)}. It closes after boot has been completed
	 */
	public PrintStream bootLogger;
	/**
	 * wheather or not your TerminalApp should log. disabled by default in case users want to use jredfox logger or log4j
	 */
	public boolean shouldLog = false;
	
	public TerminalApp(String id, String name, String version)
	{
		this(id, name, version, false);
	}
	
	public TerminalApp(String id, String n, String v, boolean force)
	{
		this(id, n, v, force, true);
	}
	
	public TerminalApp(String id, String name, String version, boolean force, boolean pause)
	{
		assert !id.contains(" ") : "Terminal app id cannot contain spaces!";
		assert !name.contains("\"") : "Terminal app name cannot contain double quotes!";
		this.id = id;
		this.name = name;
		this.version = version;
		this.force = force;
		this.pause = pause;
	}

	public String getTitle()
	{
		return this.name + " " + this.version;
	}
	
	/**
	 * return the profile based on the OS
	 */
	public Profile getProfile()
	{
		return null;
	}
	
	/**
	 * returns the specified ConsoleHost aka the UI for the terminal
	 * @return null if you want it to use start commands instead of specifying the UI type
	 */
	public ConsoleHost getConsoleHost()
	{
		if(this.conHost != null)
		{
			switch(this.conHost)
			{
				case "wt":
					return new WTHost(this);
				case "":
					break;
				default:
					throw new IllegalArgumentException("no console host handler registered for:\"" + this.conHost + "\"");
			}
		}
		return null;
	}
	
	public TerminalExe getTerminalExe()
	{
		switch(this.terminal)
		{
			case "cmd":
				return new BatchExe(this);
			case "powershell":
				System.out.println("powershell is very buggy when it comes to the start-process command it's not recommended as a default terminal for your java application!");
				return new PowerShellExe(this);
			case "Terminal.app":
			{
				return new MacBashExe(this);
			}
		}
		if(TerminalUtil.linux_terminals.contains(this.terminal))
		{
			if(this.linuxCmdsExe.contains(this.terminal))
				return new LinuxCmdTerminalExe(this);
			
			switch(this.terminal)
			{
				case "guake":
					return new GuakeTerminalExe(this);
				case "tilda":
					return new TildaTerminalExe(this);
				case "sakura":
					return new LinuxCmdTerminalExe(this);
				case "kgx":
					return new LinuxCmdTerminalExe(this);
				case "terminus":
					return new LinuxCmdTerminalExe(this);
				case "terminology":
					return new LinuxCmdTerminalExe(this);
				case "vala-terminal":
					return new ValaTerminalExe(this);
			}
			LinuxBashExe bash = new LinuxBashExe(this);
			if(this.terminal.equals("terminalpp"))
				bash.quoteCmd = true;
			return bash;
		}
		return null;
	}
	
	/**
	 * load the configurations for this terminal app
	 */
	public void load()
	{
		this.loadConfig();
		this.enableLoggers();
	}

	public void enableLoggers() 
	{
		if(this.shouldLog)
		{
			try
			{
				File log = new File(OTConstants.home, "logs/" + this.id + "/latest.log");
				FileUtil.create(log);
				System.setOut(new WrappedPrintStream(System.out, log));
				System.setErr(new WrappedPrintStream(System.err, log));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void loadConfig() 
	{
		MapConfig cfg = new MapConfig(new File(OTConstants.configs, this.id + ".cfg"));
		cfg.load();
		this.terminal = cfg.get("terminal", this.terminal).trim();
		this.conHost = cfg.get("conHost", this.conHost).trim();
		if(!TerminalUtil.isExeValid(this.terminal))
		{
			this.terminal = TerminalUtil.getTerminal();
			cfg.set("terminal", this.terminal);
		}
		if(!this.conHost.isEmpty() && !TerminalUtil.isExeValid(this.conHost))
		{
			this.conHost = "";
			cfg.set("conHost", this.conHost);
		}
		this.ANSI4BIT = cfg.get("ANSI-4bit-Colors", this.ANSI4BIT);
		if(this.ANSI4BIT)
			this.colors.setColorMode(TermColors.ANSI4BIT);
		
		String[] lcmds = cfg.get("linuxCmdExe", "").split(";");
		for(String c : lcmds)
		{
			c = c.trim();
			if(!c.isEmpty())
				this.linuxCmdsExe.add(c);
		}
		
		String[] lflags = cfg.get("linuxFlags", "").split(";");
		for(String f : lflags)
		{
			f = f.trim();
			if(f.isEmpty())
				continue;
			String[] arr = f.split("=");
			this.linuxFlags.put(arr[0].trim(), arr[1].trim());
		}
		cfg.save();
	}

	/**
	 * execute the command in the terminal UI TerminalApp configurations override
	 */
	public String getLinuxExe()
	{
		String cflag = this.linuxFlags.get(this.terminal);
		return cflag != null ? cflag : TerminalUtil.getLinuxExe(this.terminal);
	}
	
	public static class Profile
	{
		public Color bg;
		public Color fg;
		public String ansiFormat;
		public String wtTab;//WT Tab color
		public String wtScheme;//WT color scheme
		public boolean wtFullScreen;
		public boolean wtMaximized;
		/**
		 * the profile name will be equal to the id for custom profiles due to importing is always the file name of the import
		 */
		public String mac_profileName = "";
		/**
		 * make sure you set this unique to your application so it doesn't conflict with another custom terminal profile
		 */
		public String mac_profileId = "";
		/**
		 * the terminal profile path within your jar from the root directory
		 */
		public String mac_profilePath = "";
		
		public Profile()
		{
			
		}
		
		public Profile(Color b, Color t)
		{
			this.bg = b;
			this.fg = t;
		}
		
		/**
		 * pre-determined macOs terminal profile the End-USER will have this profile installed before executing your program
		 */
		public static Profile newMac(String profileName)
		{
			Profile p = new Profile();
			p.mac_profileName = profileName;
			return p;
		}
		
		/**
		 * create your new macOs terminal custom profile
		 */
		public static Profile newMac(String prid, String pp)
		{
			Profile p = new Profile();
			p.mac_profileName = prid;
			p.mac_profileId = prid;
			p.mac_profilePath = pp;
			return p;
		}
	}

	/**
	 * @return shell true color or if in ansi 4 bit mode it will return ansi 4 bit colors
	 */
	public String getBootTrueColor(Profile p)
	{
		return p == null ? "" : this.colors.formatColor(this.ANSI4BIT ? TermColors.ANSI4BIT : TermColors.TRUE_COLOR, p.bg, p.fg, p.ansiFormat, false);
	}
	
	/**
	 * get the lower resolution color that's non rgb. either ANSI 4bit or xterm-256 for the shell
	 */
	public String getBootPaletteColor(Profile p)
	{
		return p == null ? "" : this.colors.formatColor(this.ANSI4BIT ? TermColors.ANSI4BIT : TermColors.XTERM_256, p.bg, p.fg, p.ansiFormat, false);
	}

	public PrintStream getBootLogger() 
	{
		try
		{
			File flog = new File(OTConstants.home, "logs/" + id + "/boot-" + (OTConstants.LAUNCHED ? "client" : "host") + ".txt");
			FileUtil.create(flog);
			this.bootLogger = new PrintStream(new FileOutputStream(flog), true);
			return this.bootLogger;
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
