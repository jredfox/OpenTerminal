package jredfox.terminal.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jredfox.common.io.IOUtils;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.OpenTerminalConstants;

public class TerminalApp {
	
	public String terminal;
	public String uuid;
	public boolean background;
	public boolean shouldPause;
	
	public String id;
	public String shName;
	public String name;
	public String version;
	public Class<?> mainClass;
	public boolean runDeob;
	public boolean forceTerminal;//set this to true to always open up a new window
	
	//args
	public List<String> jvmArgs = new ArrayList<>();
	public List<String> programArgs = new ArrayList<>();
	public LinkedHashMap<String, String> properties = new LinkedHashMap<>();
	
	//non serializable vars
	public boolean compiled;//is this app compiled into a jar already
	public Process process;
	public static final Set<String> props = new HashSet<>();
	
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
    	
		//non properties vars
		this.programArgs = new ArrayList<>(args.length);
		for(String s : args)
		{
			if(isProperty(s))
				this.parseProperty(s);
			else
				this.programArgs.add(s);
		}
		this.jvmArgs = JavaUtil.asArray(JREUtil.getJVMArgs());
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
		this.uuid = isLaunching ? this.getProperty("openterminal.uuid", this.genUUID()) : this.getProperty("openterminal.uuid");
		this.background = this.getProperty("openterminal.background", false);
		this.shouldPause = this.getProperty("openterminal.shoulPause", pause);
	}

	/**
	 * parsed from a reboot file
	 */
	public TerminalApp(File propsFile)
	{
		this.parseProperties(propsFile);
		this.fromProperties();
	}

	public boolean shouldOpen()
    {
        return !this.background && (!this.compiled ? this.runDeob && System.console() == null && !JREUtil.isDebugMode() : this.forceTerminal || System.console() == null);
    }
    
	public boolean shouldPause() 
	{
		return this.shouldPause;
	}
	
	/**
	 * use this boolean to load configs related to the TerminalApp's variables
	 */
	public boolean isLaunching()
	{
		String prop = JavaUtil.getFirst(this.properties);
		return prop == null || OpenTerminalConstants.rebooted.equals(prop);
	}
	
	/**
	 * is your TerminalApp already a child process? Is your current code executing from not the Launcher but, a child process?
	 */
	public boolean isChild()
	{
		String prop = JavaUtil.getFirst(this.properties);
		return prop != null && (prop.equals(OpenTerminalConstants.launched) || prop.equals(OpenTerminalConstants.wrapped) || prop.equals(OpenTerminalConstants.rebooted));
	}
	
	/**
	 * can your main(String[] args) execute yet? First launch fires launcher, Second launch fires virtual wrapper, Third launch your program now executes as normal
	 */
	public boolean isWrapped()
	{
		String prop = JavaUtil.getFirst(this.properties);
		return OpenTerminalConstants.wrapped.equals(prop);
	}
	
	public void reboot()
	{
		JavaUtil.setFirst(this.properties, OpenTerminalConstants.rebooted);
		//TODO:
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
	 * generate an instance for this app to instantiate in
	 */
	protected String genUUID() 
	{
		UUID id = null;
		while(id == null)
		{
			UUID tmp = UUID.randomUUID();
			id = new File(this.getRootAppData(), tmp.toString()).exists() ? null : tmp;
		}
		return id.toString();
	}
	
	public void parseProperties(File propsFile) 
	{
		List<String> li = IOUtils.getFileLines(propsFile);
		for(String s : li)
		{
			s = s.trim();
			if(this.isProperty(s))
				this.parseProperty(s);
		}
	}

	public void parseProperty(String line)
	{
		if(!line.contains("="))
		{
			this.properties.put(line, "");
			return;
		}
		String[] arr = JavaUtil.splitFirst(line, '=', '"', '"');
		this.properties.put(arr[0], arr[1]);
	}

	public boolean getBooleanProperty(String propId)
	{
		return Boolean.parseBoolean(this.getProperty(propId));
	}
	
	public boolean getProperty(String propId, boolean defaults)
	{
		return Boolean.parseBoolean(this.getProperty(propId, String.valueOf(defaults)));
	}
	
	public String getProperty(String propId, String defaults)
	{
		return this.properties.containsKey(propId) ? this.properties.get(propId) : defaults;
	}

	public String getProperty(String propId) 
	{
		return this.properties.get(propId);
	}

	public boolean isProperty(String s) 
	{
		return s.startsWith("openterminal.") || s.contains("=") && props.contains(s.split("=")[0]);
	}
	
	/**
	 * sync your values with the properties
	 */
	public void fromProperties()
	{
		this.terminal = this.getProperty("openterminal.terminal");
		this.uuid = this.getProperty("openterminal.uuid");
		this.background = this.getBooleanProperty("openterminal.background");
		this.shouldPause = this.getBooleanProperty("openterminal.shoulPause");
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
		this.properties.put("openterminal.terminal", this.terminal);
		this.properties.put("openterminal.uuid", this.uuid);
		this.properties.put("openterminal.background", String.valueOf(this.background));
		this.properties.put("openterminal.shoulPause", String.valueOf(this.shouldPause));
		this.properties.put("openterminal.id", this.id);
		this.properties.put("openterminal.shName", this.shName);
		this.properties.put("openterminal.name", this.name);
		this.properties.put("openterminal.version", this.version);
		this.properties.put("openterminal.mainClass", this.mainClass.getName());
		this.properties.put("openterminal.runDeob", String.valueOf(this.runDeob));
		this.properties.put("openterminal.forceTerminal", String.valueOf(this.forceTerminal));
	}
	
	public void writeProperties(Collection<String> col)
	{
		int index = 0;
		for(Map.Entry<String, String> entry : this.properties.entrySet())
		{
			if(index == 0 && entry.getKey().startsWith("openterminal.launcher."))
				continue;
			String v = entry.getValue();
			col.add(entry.getKey() + (v.isEmpty() ? "" : "=" + v));
			index++;
		}
	}
	
	/**
	 * returns the appdata contained in %appdata%/SelfCommandPrompt/appId
	 */
	public File getAppdata()
	{
		return new File(OpenTerminalConstants.data, this.uuid + "/" + this.id);
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
