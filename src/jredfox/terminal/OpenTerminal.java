package jredfox.terminal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;

public class OpenTerminal {
	
	public static OpenTerminal INSTANCE = new OpenTerminal();
	public TerminalApp app;
	public boolean canReboot = true;
	public String terminal = OSUtil.getTerminal();//TODO:
	
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
			if(this.app.properties.get(0).equals(OpenTerminalConstants.launched))
			{
				List<String> args = new ArrayList<>(this.app.properties.size() + this.app.programArgs.size());
				this.app.properties.set(0, "openterminal.wrapped");
				args.addAll(this.app.properties);
				args.addAll(this.app.programArgs);
				OpenTerminalWrapper.run(this.app, JavaUtil.toArray(args, String.class));
				return null;//when the virtual wrapper is done executing so is the process so return null as nothing happens here
			}
			else if(this.app.properties.get(0).equals(OpenTerminalConstants.wrapped))
				return this.app.getProgramArgs();
		}
		
		this.app.init(this);
		boolean open = this.shouldOpen();
		this.app.process = this.launch(open);
		int exit = this.app.process != null ? 0 : -1;
		while(this.app.process != null)
		{
			if(!this.app.process.isAlive())
			{
				exit = this.app.process.exitValue();
				this.app.process = this.canReboot && exit == 4097 ? this.relaunch(open) : null;
			}
		}
		System.out.println("shutting down OpenTerminal Launcher:" + exit);
		this.exit(exit);
		return this.app.getProgramArgs();
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
    	builder.addCommand(JREUtil.getJVMArgs());
    	builder.addCommand("-cp");
    	String q = OSUtil.getQuote();
    	builder.addCommand(q + libs + q);
    	builder.addCommand(this.app.mainClass.getName());
    	builder.addCommand(OpenTerminalConstants.launched);
    	builder.addCommand(OpenTerminalUtil.wrapProgramArgs(this.app.programArgs));
    	String command = builder.toString();
    	String shName = this.app.id.contains("/") ? JavaUtil.getLastSplit(this.app.id, "/") : this.app.id;
    	try
    	{
    		return OpenTerminalUtil.runInNewTerminal(this.getAppdata(this.app.id), this.app.terminal, this.app.name, shName, command);
//TODO:    		return open ? OpenTerminalUtil.runInNewTerminal(this.getAppdata(this.app.id), this.app.terminal, this.app.name, shName, command) : OpenTerminalUtil.runInTerminal(this.terminal, command);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
	}
	
	public Process relaunch(boolean open) 
	{
		System.out.println("re-launching");
		return this.launch(open);
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
	
	/**
	 * returns the appdata contained in %appdata%/SelfCommandPrompt/appId
	 */
	public File getAppdata(String appId)
	{
		return new File(OpenTerminalConstants.data, appId);
	}

}
