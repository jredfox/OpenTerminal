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
	
	public String terminal;
	public String idHash;
	public boolean background;
	public boolean shouldPause;
	public boolean hardPause;
	public String id;
	public String shName;
	public String name;
	public String version;
	/**
	 * the main class of your program that is never a wrapper class
	 */
	public Class<?> iclass;
	/**
	 * the main class the jvm. may be equal to your program's main class or a wrapper class
	 */
	public Class<?> mainClass;
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
	public boolean isRebooting;
	
	public TerminalApp(Class<?> iclass, String[] args)
	{
		this(iclass, suggestAppId(iclass), args);
	}
	
	public TerminalApp(Class<?> iclass, String id, String[] args)
	{
		this(iclass, id, id, "1.0.0", args);
	}
	
	public TerminalApp(Class<?> iclass, String id, String name, String version, String[] args)
	{
		this(iclass, id, name, version, JREUtil.getMainClass(), args);
	}
	
	public TerminalApp(Class<?> iclass, String id, String name, String version, Class<?> jvmMain, String[] args)
	{
		this(iclass, id, name, version, jvmMain, args, true);
	}
	
	public TerminalApp(Class<?> iclass, String id, String name, String version, Class<?> jvmMain, String[] args, boolean runDeob)
	{
    	if(JavaUtil.containsAny(id, OpenTerminalConstants.INVALID))
    		throw new RuntimeException("appId contains illegal parsing characters:(" + id + "), invalid:" + OpenTerminalConstants.INVALID);
    	
		this.jvmArgs = OpenTerminal.isInit() ? JavaUtil.asArray(JREUtil.getJVMArgs()) : new ArrayList<>();
		JavaUtil.removeStarts(this.jvmArgs, "-D" + OpenTerminalConstants.jvm, false);
		TerminalApp.addArgs(this.jvmArgs, this.getProperty(OpenTerminalConstants.jvm, ""));
		
		this.programArgs = new ArrayList<>(args.length);
		for(String s : args)
			this.programArgs.add(s);
		
		this.id = this.getProperty("ot.id", id);
		this.shName = this.getProperty("ot.shName", this.id.contains("/") ? JavaUtil.getLastSplit(this.id, "/") : this.id);
		this.name = this.getProperty("ot.name", name);
		this.version = this.getProperty("ot.version", version);
		this.iclass = JREUtil.getClass(this.getProperty("ot.iclass", iclass.getName()), true);
		this.mainClass = JREUtil.getClass(this.getProperty("ot.mainClass", jvmMain.getName()), true);
		this.runDeob = this.getProperty("ot.runDeob", runDeob);
		this.forceTerminal = this.getProperty("ot.forceTerminal", false);
		
		boolean isLaunching = OpenTerminal.isLaunching();
		if(isLaunching)
			this.syncConfig();
		this.terminal = this.getProperty("ot.terminal", this.terminal);
		this.idHash = isLaunching ? this.getProperty("ot.hash", "" + System.currentTimeMillis()) : this.getProperty("ot.hash");
		this.background = this.getProperty("ot.background", false);
		this.shouldPause = this.getProperty("ot.shoulPause", false);
		this.hardPause = this.getProperty("ot.hardPause", false);
		this.userDir = new File(this.getProperty(OpenTerminalConstants.p_userDir));
		this.userHome = new File(this.getProperty(OpenTerminalConstants.p_userHome));
		this.tmp = new File(this.getProperty(OpenTerminalConstants.p_tmp));
		this.appdata = new File(this.getProperty(OpenTerminalConstants.p_appdata));
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
    
	public boolean shouldPause() 
	{
		return this.shouldPause || this.hardPause;
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
