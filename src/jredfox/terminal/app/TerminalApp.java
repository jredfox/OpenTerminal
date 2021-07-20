package jredfox.terminal.app;

import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.OpenTerminalConstants;

public class TerminalApp {
	
	public final boolean background;//TODO:
	public String terminal;
	
	public String id;
	public String name;
	public String version;
	public Class<?> mainClass;
	public String[] programArgs;
	public String[] jvmArgs;
	public boolean runDeob;
	public boolean forceTerminal;//set this to true to always open up a new window
	
	//non serializable vars
	public boolean shouldPause;
	public boolean compiled;//is this app compiled into a jar already
	public Process process;
	
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
		this(id, name, version, clazz, args, true, true);
	}
	
	public TerminalApp(String id, String name, String version, Class<?> clazz, String[] args, boolean runDeob, boolean pause)
	{
    	if(JavaUtil.containsAny(id, OpenTerminalConstants.INVALID))
    		throw new RuntimeException("appId contains illegal parsing characters:(" + id + "), invalid:" + OpenTerminalConstants.INVALID);
		this.id = id;
		this.name = name;
		this.version = version;
		this.mainClass = clazz;
		this.programArgs = args;
		
		//run vars
		this.runDeob = runDeob;
		this.forceTerminal = false;
		this.shouldPause = pause;
		this.compiled = JREUtil.isCompiled(this.mainClass);
		this.background = false;
	}

    public boolean shouldOpen()
    {
        return !this.compiled ? this.runDeob && System.console() == null && !JREUtil.isDebugMode() : this.forceTerminal || System.console() == null;
    }
    
	public boolean shouldPause() 
	{
		return this.shouldPause;
	}
	
	public void reboot()
	{
		System.exit(4097);
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

}
