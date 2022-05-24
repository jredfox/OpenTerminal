package jml.ot;

import java.io.File;

public class TerminalApp {
	
	public String id;
	public String name;
	public String version;
	public boolean force;//when enabled will always open a window
	
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
	
	public File getHome()
	{
		return new File(OTConstants.home + "/" + this.id);
	}
	
	public static class Profile
	{
		public String bg;
		public String fg;
		public String tab_color;//windows terminal only so far
		public File termProfile;//only supported on macOs the file to apply an entire profile
		
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
