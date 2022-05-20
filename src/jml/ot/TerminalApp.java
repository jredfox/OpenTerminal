package jml.ot;

import java.awt.Color;
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
	
	class Profile
	{
		public Color bg;
		public Color txt;
		public File macProfile;
		
		public Profile(Color b, Color t)
		{
			this.bg = b;
			this.txt = t;
		}
		
		public Profile(File mp)
		{
			this.macProfile = mp;
		}
	}

}
