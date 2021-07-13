package jredfox.selfcmd;

import java.io.File;
import java.util.Set;

public class ConsoleApp {
	
	//app vars
	public String appId;
	public String appName;
	public String appVersion;
	
	//console app data
	public File appdata;//this app's appdata
	public File reboot;
	public File shellScript;//non windows
	
	//JVM
	public Set<String> programArgs;
	public Set<String> jvmArgs;
	public Process process;
	
	public ConsoleApp(String id, String name, String version, String[] args)
	{
		//app vars
		this.appId = id;
		this.appName = name;
		this.appVersion = version;
		
		//console app data
		this.appdata = SelfCommandPrompt.getAppdata(this.appId);
		this.reboot = new File(this.appdata, "reboot.jvm");
		this.shellScript = new File(this.appdata, "launch.sh");
		
		//TODO: program args
	}

}
