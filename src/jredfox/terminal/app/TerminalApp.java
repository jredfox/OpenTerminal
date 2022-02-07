package jredfox.terminal.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jredfox.common.config.MapConfig;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.OpenTerminal;
import jredfox.terminal.OpenTerminalConstants;
import jredfox.terminal.OpenTerminalUtil;

public class TerminalApp {
	
	public Class<?> appClass;//main class of your program
	public Class<?> jvmClass;//main class of your program according to JVM may be a wrapper
	public String id;
	public String name;
	public String version;
	public String terminal;
	public String idHash;
	public String shName;
	public boolean background;
	public boolean pause;
	public boolean hardPause;
	public boolean runDeob;
	public boolean forceTerminal;//set this to true to always open up a new window
	public boolean canReboot = true;
	public List<String> jvmArgs;
	public List<String> programArgs;
	public File userDir;
	public File userHome;
	public File tmp;
	public File appdata;
	
	//non serializable vars
	public boolean compiled = JREUtil.isCompiled();
	public Process process;
	
	public TerminalApp(Class<?> appClass, String[] args)
	{
		this(appClass, suggestAppId(appClass), args);
	}
	
	public TerminalApp(Class<?> appClass, String id, String[] args)
	{
		this(appClass, id, id, "1.0.0", args);
	}
	
	public TerminalApp(Class<?> appClass, String id, String name, String version, String[] args)
	{
		this(appClass, id, name, version, JREUtil.getMainClass(), args);
	}
	
	public TerminalApp(Class<?> appClass, String id, String name, String version, Class<?> jvmMain, String[] args)
	{
		this(appClass, id, name, version, jvmMain, args, true);
	}
	
	public TerminalApp(Class<?> appClass, String id, String name, String version, Class<?> jvmMain, String[] args, boolean runDeob)
	{
    	if(JavaUtil.containsAny(id, OpenTerminalConstants.INVALID))
    		throw new RuntimeException("appId contains illegal parsing characters:(" + id + "), invalid:" + OpenTerminalConstants.INVALID);
    	
    	if(OpenTerminal.isLaunching())
    		this.loadLaunch();
    	
    	this.appClass = appClass;
    	this.jvmClass = jvmMain;
    	this.id = id;
    	this.name = name;
    	this.version = version;
       	this.runDeob = runDeob;
    	this.jvmClass = jvmMain;
    	this.jvmArgs = JavaUtil.asArray(JREUtil.getJVMArgs());
    	this.programArgs = JavaUtil.asArray(args);
	}
	
	/**
	 * loads init launch data needed for launching the TerminalApp
	 */
	protected void loadLaunch()
	{
		this.syncConfig();
		this.idHash = "" + System.currentTimeMillis();
	}
	
	/**
	 * sync your global TerminalApp properties
	 */
	public void syncConfig()
	{
		File cfgFile = new File(this.getRootAppData(), this.shName + ".cfg");
		MapConfig cfg = new MapConfig(cfgFile);
		cfg.load();
		this.terminal = cfg.get("terminal", "");
		if(this.terminal.isEmpty() || !OSUtil.isTerminalValid(this.terminal))
		{
			this.terminal = OSUtil.getTerminal();
			cfg.set("terminal", this.terminal);
			cfg.save();
		}
	}

	/**
	 * should your TerminalApp open the terminal gui?
	 */
	public boolean shouldOpen()
    {
		boolean runDeob = this.runDeob && !JREUtil.isDebugMode();
		return !this.background && (this.forceTerminal ? (this.compiled || runDeob) : (System.console() == null && (this.compiled || runDeob) ));
    }
	
	public boolean shouldPause(int exit)
	{
		return exit != OpenTerminalConstants.rebootExit && exit != OpenTerminalConstants.forceExit && this.shouldPause();
	}
    
	/**
	 * pauses if pause is enabled and background is disabled
	 */
	public boolean shouldPause() 
	{
		return !this.background && (this.shouldPause || this.hardPause);
	}
	
	/**
	 * shouldPause will NOT catch {@link System#exit(int)}
	 */
	public TerminalApp enablePause()
	{
		this.shouldPause = true;
		return this;
	}
	
	/**
	 * hard pause will catch {@link System#exit(int)}
	 */
	public TerminalApp enableHardPause()
	{
		this.hardPause = true;
		return this;
	}
	
	/**
	 * disables both shouldPause and hardPause
	 */
	public void disablePause()
	{
		this.shouldPause = false;
		this.hardPause = false;
	}
	
	public TerminalApp setRebootable(boolean b)
	{
		this.canReboot = b;
		return this;
	}
	
	public boolean canReboot()
	{
		return this.canReboot;
	}
	
	/**
	 * pause at the end of the program
	 */
	public void pause()
	{
		System.out.println("Press ENTER to continue:");
		OpenTerminalConstants.scanner.nextLine();
	}
	
	/**
	 * sync user dirs from the properties. call this if you set system properties and want to reboot or use the TerminalApp directory vars again
	 */
	public void syncDirs()
	{
		this.userDir = new File(this.getProperty(OpenTerminalConstants.p_userDir));
		this.userHome = new File(this.getProperty(OpenTerminalConstants.p_userHome));
		this.tmp = new File(this.getProperty(OpenTerminalConstants.p_tmp));
		this.appdata = new File(this.getProperty(OpenTerminalConstants.p_appdata));
	}
	
	/**
	 * returns the appdata current instance
	 */
	public File getAppdata()
	{
		return new File(OpenTerminalConstants.data, this.id + "/instances/" + this.idHash);
	}
	
	/**
	 * returns the global appdata folder for this application
	 */
	public File getRootAppData()
	{
		return new File(OpenTerminalConstants.data, this.id);
	}
	
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId(Class<?> clazz)
	{
		return suggestAppId(clazz.getName());
	}
	
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId(String name)
	{
		return name.replaceAll("\\.", "/");
	}

	public String[] getProgramArgs() 
	{
		return JavaUtil.toArray(this.programArgs, String.class);
	}
	
	public static TerminalApp fromProperties(String[] args) 
	{
		return JREUtil.newInstance(JREUtil.getClass(System.getProperty("ot.appClass"), true), new Class<?>[]{Class.class, String[].class}, (Object)args);
	}


}
