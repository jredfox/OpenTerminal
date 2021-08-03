package jredfox.terminal.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.io.IOUtils;
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
	public File userDir;
	public File userHome;
	public File tmp;
	public File appdata;
	
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
		
		this.userDir = new File(this.getProperty(OpenTerminalConstants.p_userDir));
		this.userHome = new File(this.getProperty(OpenTerminalConstants.p_userHome));
		this.tmp = new File(this.getProperty(OpenTerminalConstants.p_tmp));
		this.appdata = new File(this.getProperty(OpenTerminalConstants.p_appdata));
		
		this.id = this.getProperty("openterminal.id", id);
		this.shName = this.getProperty("openterminal.shName", this.id.contains("/") ? JavaUtil.getLastSplit(this.id, "/") : this.id);
		this.name = this.getProperty("openterminal.name", name);
		this.version = this.getProperty("openterminal.version", version);
		this.mainClass = JREUtil.getClass(this.getProperty("openterminal.mainClass", clazz.getName()), true);
		this.runDeob = this.getProperty("openterminal.runDeob", runDeob);
		this.forceTerminal = this.getProperty("openterminal.forceTerminal", false);
		
		boolean isLaunching = OpenTerminal.isLaunching();
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
	 * write your app vars to jvm properties for wrapping/execution
	 */
	public void writeProperties(List<String> jvm, ExeBuilder builder)
	{
		//preserve the user directories that have just gotten edited
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.p_userDir, this.userDir.getPath()));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.p_userHome, this.userHome.getPath()));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.p_tmp, this.tmp.getPath()));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.p_appdata, this.appdata.getPath()));
		
		//preserve the app properties
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.appClass", this.getClass().getName()));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.terminal", this.terminal));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.hash", "" + this.idHash));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.background", "" + this.background));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.shoulPause", "" + this.shouldPause));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.hardPause", "" + this.hardPause));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.id", this.id));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.shName", this.shName));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.name", this.name));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.version", this.version));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.mainClass", this.mainClass.getName()));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.runDeob", "" + this.runDeob));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.forceTerminal", "" + this.forceTerminal));
		builder.addCommand(OpenTerminalUtil.writeProperty(jvm, "openterminal.canReboot", "" + this.canReboot));
	}
	
	/**
	 * reboot your TerminalApp
	 */
	public void reboot()
	{
		File reboot = new File(this.getAppdata(), "reboot.properties");
		List<String> li = new ArrayList<>();
		
		//preserve the user directories that have just gotten edited
		li.add(OpenTerminalConstants.p_userDir + "=" + this.userDir.getPath());
		li.add(OpenTerminalConstants.p_userHome + "=" + this.userHome.getPath());
		li.add(OpenTerminalConstants.p_tmp + "=" + this.tmp.getPath());
		li.add(OpenTerminalConstants.p_appdata + "=" + this.appdata.getPath());
		
		//preserve the app properties
		li.add("openterminal.appClass" + "=" + this.getClass().getName());
		li.add("openterminal.terminal" + "=" + this.terminal);
		li.add("openterminal.hash" + "=" + this.idHash);
		li.add("openterminal.background" + "=" + this.background);
		li.add("openterminal.shoulPause" + "=" + this.shouldPause);
		li.add("openterminal.hardPause" + "=" + this.hardPause);
		li.add("openterminal.id" + "=" + this.id);
		li.add("openterminal.shName" + "=" + this.shName);
		li.add("openterminal.name" + "=" + this.name);
		li.add("openterminal.version" + "=" + this.version);
		li.add("openterminal.mainClass" + "=" + this.mainClass.getName());
		li.add("openterminal.runDeob" + "=" + this.runDeob);
		li.add("openterminal.forceTerminal" + "=" + this.forceTerminal);
		li.add("openterminal.canReboot" + "=" + this.canReboot);
		
		//preserve the jvm and program args
		li.add(OpenTerminalConstants.jvmArgs + "=" + OpenTerminalUtil.wrapArgsToCmd(this.jvmArgs).replaceAll(System.lineSeparator(), OpenTerminalConstants.linefeed));
		li.add(OpenTerminalConstants.programArgs + "=" + OpenTerminalUtil.wrapArgsToCmd(this.programArgs).replaceAll(System.lineSeparator(), OpenTerminalConstants.linefeed));//stop illegal line feed characters from messing up parsing
		
		IOUtils.saveFileLines(li, reboot, true);
		JREUtil.shutdown(OpenTerminalConstants.rebootExit);
	}
	
	public static void parseProperties(File propsFile) 
	{
		List<String> lines = IOUtils.getFileLines(propsFile);
		for(String s : lines)
		{
			s = s.trim().replaceAll(OpenTerminalConstants.linefeed, System.lineSeparator());
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
		this.userDir = new File(this.getProperty(OpenTerminalConstants.p_userDir));
		this.userHome = new File(this.getProperty(OpenTerminalConstants.p_userHome));
		this.tmp = new File(this.getProperty(OpenTerminalConstants.p_tmp));
		this.appdata = new File(this.getProperty(OpenTerminalConstants.p_appdata));
		this.id = this.getProperty("openterminal.id");
		this.shName = this.getProperty("openterminal.shName");
		this.name = this.getProperty("openterminal.name");
		this.version = this.getProperty("openterminal.version");
		this.mainClass = JREUtil.getClass(this.getProperty("openterminal.mainClass"), true);
		this.runDeob = this.getBooleanProperty("openterminal.runDeob");
		this.forceTerminal = this.getBooleanProperty("openterminal.forceTerminal");
		this.canReboot = this.getBooleanProperty("openterminal.canReboot");
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

	public static TerminalApp fromFile(File reboot)
	{
		TerminalApp.parseProperties(reboot);
		TerminalApp app = TerminalApp.newInstance();
		app.fromProperties();
		addArgs(app.jvmArgs, OpenTerminalConstants.jvmArgs);
		addArgs(app.programArgs, OpenTerminalConstants.programArgs);
		System.clearProperty(OpenTerminalConstants.jvmArgs);
		System.clearProperty(OpenTerminalConstants.programArgs);
		return app;
	}
	
	public static TerminalApp newInstance() 
	{
		return JREUtil.newInstance(JREUtil.getClass(System.getProperty("openterminal.appClass"), true));
	}
	
	public static TerminalApp newInstance(String[] args) 
	{
		return JREUtil.newInstance(JREUtil.getClass(System.getProperty("openterminal.appClass"), true), new Class<?>[]{String[].class}, (Object)args);
	}

	private static void addArgs(List<String> args, String argId) 
	{
		String[] arr = System.getProperty(argId).split(" ");
		for(String s : arr)
			args.add(s.replaceAll(OpenTerminalConstants.spacefeed, " "));
	}


}
