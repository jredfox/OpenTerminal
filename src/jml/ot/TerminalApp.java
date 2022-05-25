package jml.ot;

import java.io.File;
import java.io.IOException;

import jml.ot.app.BatchExe;
import jml.ot.app.PowerShellExe;
import jml.ot.app.TerminalExe;

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
	
	public TerminalExe getTerminal() throws IOException
	{
		switch(this.terminal)
		{
			case "cmd":
				return new BatchExe(this);
			case "powershell":
				System.out.println("powershell is very buggy when it comes to the start-process command it's not recommended as a default terminal for your java application!");
				return new PowerShellExe(this);
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
		public String tab_color;//windows terminal only so far
		public String wtScheme;//WT only
		public File termProfile;//only supported on macOs the file to apply an entire profile
		
		public Profile()
		{
			
		}
		
		public Profile(String b, String f)
		{
			this.bg = b;
			this.fg = f;
		}
		
		public Profile tabColor(String t)
		{
			this.tab_color = t;
			return this;
		}
		
		public Profile(File mp)
		{
			this.termProfile = mp;
		}
	}

}
