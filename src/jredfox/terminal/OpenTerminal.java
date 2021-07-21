package jredfox.terminal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;

public class OpenTerminal {
	
	public static OpenTerminal INSTANCE = new OpenTerminal();
	public TerminalApp app;
	public boolean canReboot = true;
	
	public OpenTerminal()
	{
		
	}
	
	public String[] run(TerminalApp app)
	{
		this.app = app;
		return this.run();
	}

	public String[] run() 
	{
		if(this.app == null)
			throw new IllegalArgumentException("TerminalApp cannot be null!");
		else if(!this.app.properties.isEmpty())
		{
			if(this.app.properties.containsKey(OpenTerminalConstants.launched))
			{
				List<String> args = new ArrayList<>(this.app.properties.size() + this.app.programArgs.size());
				args.add(OpenTerminalConstants.wrapped);
				this.app.writeProperties(args);
				args.addAll(this.app.programArgs);
				OpenTerminalWrapper.run(this.app, JavaUtil.toArray(args, String.class));
				return null;//return null as the TerminalApp is done executing and System#exit has already been called on the child process
			}
			else if(this.app.properties.containsKey(OpenTerminalConstants.wrapped))
				return this.app.getProgramArgs();
		}
		
		this.app.toProperties();
		this.app.process = this.launch(this.shouldOpen());
		int exit = this.app.process != null ? 0 : -1;
		while(this.app.process != null)
		{
			if(!this.app.process.isAlive())
			{
				exit = this.app.process.exitValue();
				File reboot = new File(this.app.getAppdata(), "reboot.properties");
				this.app.process = this.canReboot && reboot.exists() ? this.relaunch(reboot) : null;
			}
		}
		System.out.println("shutting down OpenTerminal Launcher:" + exit);
		this.exit(exit);
		return null;//return null as the Launcher is done executing and System#exit has already been called
	}

	/**
	 * give the OpenTerminal launcher a chance to deny app privileges
	 */
	public boolean shouldOpen()
	{
		return this.app.shouldOpen();
	}
	
	public Process launch(boolean open)
	{
        String libs = System.getProperty("java.class.path");
        if(JavaUtil.containsAny(libs, OpenTerminalConstants.INVALID))
        	throw new RuntimeException("one or more LIBRARIES contains illegal parsing characters:(" + libs + "), invalid:" + OpenTerminalConstants.INVALID);
        
        ExeBuilder builder = new ExeBuilder();
    	builder.addCommand("java");
    	builder.addCommand(this.app.jvmArgs);
    	builder.addCommand("-cp");
    	String q = OSUtil.getQuote();
    	builder.addCommand(q + libs + q);
    	builder.addCommand(this.app.mainClass.getName());
    	builder.addCommand(OpenTerminalConstants.launched);
    	this.app.writeProperties(builder.commands);
    	builder.addCommand(OpenTerminalUtil.wrapProgramArgs(this.app.programArgs));
    	String command = builder.toString();
    	try
    	{
    		return OpenTerminalUtil.runInNewTerminal(this.app.getAppdata(), this.app.terminal, this.app.name, this.app.shName, command);
//TODO:    		return open ? OpenTerminalUtil.runInNewTerminal(this.getAppdata(this.app.id), this.app.terminal, this.app.name, shName, command) : OpenTerminalUtil.runInTerminal(this.terminal, command);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
	}
	
	/**
	 * use {@link TerminalApp#reboot()} for users. This is to simply re-launch your TerminalApp after the reboot file has started from the Parent(Launcher) process not your TerminalApp(child) process
	 */
	public Process relaunch(File reboot) 
	{
		System.out.println("re-launching");
		return null;
	}

	public void exit(int code)
	{
		System.exit(code);
	}
	
	public boolean canReboot()
	{
		return this.canReboot;
	}
	
	public void setCanReboot(boolean b)
	{
		this.canReboot = b;
	}

}
