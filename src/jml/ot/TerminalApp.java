package jml.ot;

import java.io.File;
import java.io.IOException;

import jml.ot.terminal.BatchExe;
import jml.ot.terminal.MacBashExe;
import jml.ot.terminal.PowerShellExe;
import jml.ot.terminal.TerminalExe;
import jml.ot.terminal.host.ConsoleHost;
import jml.ot.terminal.host.WTHost;

public class TerminalApp {
	
	public String id;
	public String name;
	public String version;
	public boolean force;//when enabled will always open a window
	public String terminal = OpenTerminal.terminal;
	public String conHost = OpenTerminal.console_host;
	
	public TerminalApp(String id, String name, String version)
	{
		this(id, name, version, true);
	}
	
	public TerminalApp(String id, String name, String version, boolean force)
	{
		assert !id.contains(" ") : "Terminal app id cannot contain spaces!";
		assert !name.contains("\"") : "Terminal app name cannot contain double quotes!";
		this.id = id;
		this.name = name;
		this.version = version;
		this.force = force;
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
			}
		}
		return null;
	}
	
	public TerminalExe getTerminalExe() throws IOException
	{
		switch(this.terminal)
		{
			case "cmd":
				return new BatchExe(this);
			case "powershell":
				System.out.println("powershell is very buggy when it comes to the start-process command it's not recommended as a default terminal for your java application!");
				return new PowerShellExe(this);
			case "/bin/bash":
			{
				return new MacBashExe(this);
			}
		}
		return null;
	}
	
	public File getHome()
	{
		return new File(OTConstants.home + "/" + this.id);
	}
	
	public static class Profile
	{
		public String bg;
		public String fg;
		public String wtTab;//WT Tab color
		public String wtScheme;//WT only
		public boolean wtFullScreen;
		public boolean wtMaximized;
		public String profileName;
		public String profileId;
		public String profilePath;
		
		public Profile()
		{
			
		}
		
		public Profile(String b, String f)
		{
			this.bg = b;
			this.fg = f;
		}
		
		public Profile(String pn, String pid, String pp)
		{
			this.profileName = pn;
			this.profileId = pid;
			this.profilePath = pp;
		}

		public String getMacProfileName()
		{
			return this.profileName;
		}
	}

}
