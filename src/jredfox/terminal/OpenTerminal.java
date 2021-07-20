package jredfox.terminal;

import java.io.File;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;

public class OpenTerminal {
	
	static
	{
		//patch macOs returning junk #untested before Big Sur
		String dir = System.getProperty("user.dir");
		String tmp = System.getProperty("java.io.tmpdir");
		if(dir.contains(tmp) && !dir.startsWith(tmp))
			JREUtil.syncUserDirWithJar();
	}
	
	public static OpenTerminal INSTANCE = new OpenTerminal();
	public TerminalApp app;
	public boolean canReboot = true;
	public String terminal = OSUtil.getTerminal();//TODO:
	
	public OpenTerminal()
	{
		
	}
	
	public void run(TerminalApp app)
	{
		this.app = app;
		this.run();
	}

	public void run() 
	{
		if(JREUtil.getMainClass().equals(OpenTerminalWrapper.class))
			return;
		else if(this.app == null)
			throw new IllegalArgumentException("TerminalApp cannot be null!");
		
		this.app.terminal = this.terminal;
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
		this.exit(exit);
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
    	builder.addCommand(OpenTerminalWrapper.class.getName());
    	builder.addCommand(this.app.mainClass.getName());
    	builder.addCommand(OpenTerminalUtil.wrapProgramArgs(this.app.programArgs));
    	String command = builder.toString();
    	String shName = this.app.id.contains("/") ? JavaUtil.getLastSplit(this.app.id, "/") : this.app.id;
    	try
    	{
    		return OpenTerminalUtil.runInNewTerminal(this.getAppdata(this.app.id), this.app.terminal, this.app.name, shName, command);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
	}
	
	public Process relaunch(boolean open) 
	{
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
	
	/**
	 * returns the appdata contained in %appdata%/SelfCommandPrompt/appId
	 */
	public File getAppdata(String appId)
	{
		return new File(OpenTerminalConstants.data, appId);
	}

}
