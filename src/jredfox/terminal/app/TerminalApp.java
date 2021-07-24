package jredfox.terminal.app;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.io.IOUtils;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
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
	public Class<?> mainClass;
	public boolean runDeob;
	public boolean forceTerminal;//set this to true to always open up a new window
	public boolean canReboot = true;
	
	//args
	public List<String> jvmArgs = new ArrayList<>();
	public List<String> programArgs = new ArrayList<>();
	
	//non serializable vars
	public boolean compiled;//is this app compiled into a jar already
	public Process process;
	public static final Set<String> props = new HashSet<>();
	
	static
	{
		String stage = System.getProperty(OpenTerminalConstants.launchStage);
		if(stage == null)
			System.setProperty(OpenTerminalConstants.launchStage, OpenTerminalConstants.init);
	}
	
	public TerminalApp(String[] args)
	{
		this(suggestAppId(), args);
	}
	
	public TerminalApp(String id, String[] args)
	{
		this(id, id, "1.0.0", args);
	}
	
	public TerminalApp(String id, String name, String version, String[] args)
	{
		this(id, name, version, JREUtil.getMainClass(), args);
	}
	
	public TerminalApp(String id, String name, String version, Class<?> clazz, String[] args)
	{
		this(id, name, version, clazz, args, true);
	}
	
	public TerminalApp(String id, String name, String version, Class<?> clazz, String[] args, boolean runDeob)
	{
    	if(JavaUtil.containsAny(id, OpenTerminalConstants.INVALID))
    		throw new RuntimeException("appId contains illegal parsing characters:(" + id + "), invalid:" + OpenTerminalConstants.INVALID);
    	
		//non properties vars
		this.programArgs = new ArrayList<>(args.length);
		for(String s : args)
			this.programArgs.add(s);
		this.jvmArgs = JREUtil.getJVMArgs();
		this.compiled = JREUtil.isCompiled();
		
		this.id = this.getProperty("openterminal.id", id);
		this.shName = this.getProperty("openterminal.shName", this.id.contains("/") ? JavaUtil.getLastSplit(this.id, "/") : this.id);
		this.name = this.getProperty("openterminal.name", name);
		this.version = this.getProperty("openterminal.version", version);
		this.mainClass = JREUtil.getClass(this.getProperty("openterminal.mainClass", clazz.getName()), true);
		this.runDeob = this.getProperty("openterminal.runDeob", runDeob);
		this.forceTerminal = this.getProperty("openterminal.forceTerminal", false);
		
		boolean isLaunching = this.isLaunching();
		this.terminal = isLaunching ? this.getProperty("openterminal.terminal", OSUtil.getTerminal()) : this.getProperty("openterminal.terminal");//TODO: get the terminal per app config and pull the terminal from the global one
		this.idHash = isLaunching ? this.getProperty("openterminal.hash", "" + System.currentTimeMillis()) : this.getProperty("openterminal.hash");
		this.background = this.getProperty("openterminal.background", false);
		this.shouldPause = this.getProperty("openterminal.shoulPause", false);
		this.hardPause = this.getProperty("openterminal.hardPause", false);
	}
	
	/**
	 * TerminalApp's when they get parsed from either the hard wrapper or a reboot
	 */
	public TerminalApp()
	{
		
	}

	/**
	 * should your TerminalApp open the terminal gui?
	 */
	public boolean shouldOpen()
    {
        return !this.background && (!this.compiled ? this.runDeob && System.console() == null && !JREUtil.isDebugMode() : this.forceTerminal || System.console() == null);
    }
	
	public boolean shouldPause(int exit)
	{
		return exit != OpenTerminalConstants.rebootExit && this.shouldPause();
	}
    
	public boolean shouldPause() 
	{
		return this.shouldPause || this.hardPause;
	}
	
	/**
	 * hard pause will catch {@link System#exit(int)}
	 */
	public TerminalApp enablePause()
	{
		this.shouldPause = true;
		return this;
	}
	
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
	
	/**
	 * use this boolean to load configs related to the TerminalApp's variables
	 */
	public boolean isLaunching()
	{
		return System.getProperty(OpenTerminalConstants.launchStage).equals(OpenTerminalConstants.init);
	}
	
	/**
	 * is your TerminalApp already a child process? Is your current code executing from not the Launcher but, a child process?
	 */
	public boolean isChild()
	{
		return !this.isLaunching();
	}
	
	/**
	 * can your main(String[] args) execute yet? First launch fires launcher, Second launch fires virtual wrapper, Third launch your program now executes as normal
	 */
	public boolean canExe()
	{
		return System.getProperty(OpenTerminalConstants.launchStage).equals(OpenTerminalConstants.exe);
	}
	
	public boolean canReboot()
	{
		return this.canReboot;
	}
	
	/**
	 * edit your variables before calling this will update the reboot vars
	 */
	public void reboot()
	{
		File reboot = new File(this.getAppdata(), "reboot.properties");
		List<String> li = new ArrayList<>();
		li.add("openterminal.appClass=" + this.getClass().getName());
		li.add("openterminal.terminal=" + this.terminal);
		li.add("openterminal.hash=" + this.idHash);
		li.add("openterminal.background=" + this.background);
		li.add("openterminal.shoulPause=" + this.shouldPause);
		li.add("openterminal.id=" + this.id);
		li.add("openterminal.shName=" + this.shName);
		li.add("openterminal.name=" + this.name);
		li.add("openterminal.version=" + this.version);
		li.add("openterminal.mainClass=" + this.mainClass.getName());
		li.add("openterminal.runDeob=" + this.runDeob);
		li.add("openterminal.forceTerminal=" + this.forceTerminal);
		li.add(OpenTerminalConstants.jvmArgs + "=" + OpenTerminalUtil.wrapArgsToCmd(this.jvmArgs));
		li.add(OpenTerminalConstants.programArgs + "=" + OpenTerminalUtil.wrapArgsToCmd(this.programArgs));
		IOUtils.saveFileLines(li, reboot, true);
		JREUtil.shutdown(OpenTerminalConstants.rebootExit);
	}
	
	/**
	 * pause at the end of the program
	 */
	public void pause()
	{
		System.out.println("Press ENTER to continue:");
		OpenTerminalConstants.scanner.nextLine();
	}
	
	public static void parseProperties(File propsFile) 
	{
		List<String> lines = IOUtils.getFileLines(propsFile);
		for(String s : lines)
		{
			s = s.trim();
			String[] arr = JavaUtil.splitFirst(s, '=', '"', '"');
			String propId = arr[0];
			String value = arr[1];
			System.setProperty(propId, value);
		}
	}
	
	/**
	 * sync your values with the properties
	 */
	public void fromProperties()
	{
		this.terminal = this.getProperty("openterminal.terminal");
		this.idHash = this.getProperty("openterminal.hash");
		this.background = this.getBooleanProperty("openterminal.background");
		this.shouldPause = this.getBooleanProperty("openterminal.shoulPause");
		this.hardPause = this.getBooleanProperty("openterminal.hardPause");
		this.id = this.getProperty("openterminal.id");
		this.shName = this.getProperty("openterminal.shName");
		this.name = this.getProperty("openterminal.name");
		this.version = this.getProperty("openterminal.version");
		this.mainClass = JREUtil.getClass(this.getProperty("openterminal.mainClass"), true);
		this.runDeob = this.getBooleanProperty("openterminal.runDeob");
		this.forceTerminal = this.getBooleanProperty("openterminal.forceTerminal");
	}
	
	/**
	 * sync your properties from the real values
	 * NOTE: doesn't writer openterminal.jvmArgs or openterminal.programArgs
	 */
	public void toProperties()
	{
		System.setProperty("openterminal.appClass", this.getClass().getName());
		System.setProperty("openterminal.terminal", this.terminal);
		System.setProperty("openterminal.hash", this.idHash);
		System.setProperty("openterminal.background", String.valueOf(this.background));
		System.setProperty("openterminal.shoulPause", String.valueOf(this.shouldPause));
		System.setProperty("openterminal.hardPause", String.valueOf(this.hardPause));
		System.setProperty("openterminal.id", this.id);
		System.setProperty("openterminal.shName", this.shName);
		System.setProperty("openterminal.name", this.name);
		System.setProperty("openterminal.version", this.version);
		System.setProperty("openterminal.mainClass", this.mainClass.getName());
		System.setProperty("openterminal.runDeob", String.valueOf(this.runDeob));
		System.setProperty("openterminal.forceTerminal", String.valueOf(this.forceTerminal));
	}
	
	public void writeProperties(List<String> jvm, ExeBuilder builder)
	{
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.appClass"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.terminal"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.hash"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.background"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.shoulPause"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.hardPause"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.id"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.shName"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.name"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.version"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.mainClass"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.runDeob"));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.forceTerminal"));
	}
	
	public boolean getProperty(String propId, boolean b)
	{
		return Boolean.parseBoolean(this.getProperty(propId, String.valueOf(b)));
	}

	public String getProperty(String propId, String defaults) 
	{
		String p = System.getProperty(propId);
		return p != null ? p : defaults;
	}

	public boolean getBooleanProperty(String propId) 
	{
		return Boolean.parseBoolean(this.getProperty(propId));
	}

	public String getProperty(String propId)
	{
		return System.getProperty(propId);
	}
	
	/**
	 * returns the appdata contained in %appdata%/SelfCommandPrompt/appId
	 */
	public File getAppdata()
	{
		return new File(OpenTerminalConstants.data, this.id + "/" + this.idHash);
	}
	
	public File getRootAppData()
	{
		return new File(OpenTerminalConstants.data, this.id);
	}
    
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId()
	{
		return suggestAppId(JREUtil.getMainClassName());
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

}
