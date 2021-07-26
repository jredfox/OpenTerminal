package jredfox.terminal;

import java.io.File;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;

public class OpenTerminal {
	
	public static OpenTerminal INSTANCE = new OpenTerminal();
	public TerminalApp app;
	
	public OpenTerminal()
	{
		
	}
	
	public void run(TerminalApp app)
	{
		this.app = app;
		this.run();
	}

	protected void run()
	{
		if(this.app == null)
			throw new IllegalArgumentException("TerminalApp cannot be null!");
		
		String stage = System.getProperty(OpenTerminalConstants.launchStage);
		if(stage.equals(OpenTerminalConstants.exe))
			return;
		else if(stage.equals(OpenTerminalConstants.wrapping))
		{
			OpenTerminalWrapper.run(this.app);
			return;//return as the TerminalApp is done executing and System#exit has already been called on the child process
		}
		
		this.app.process = this.launch(this.shouldOpen());
		if(this.app.process != null)
			JREUtil.sleep(700);
		int exit = this.app.process != null ? 0 : -1;
		while(this.app.process != null)
		{
			if(!this.app.process.isAlive())
			{
				File reboot = new File(this.app.getAppdata(), "reboot.properties");
				if(this.app.canReboot() && reboot.exists())
				{
					this.relaunch(reboot);
					JREUtil.sleep(700);
					continue;
				}
				exit = this.app.process.exitValue();
				this.app.process = null;
			}
			else
				JREUtil.sleep(1000);
		}
		System.out.println("shutting down OpenTerminal Launcher:" + exit);
		this.exit(exit);
	}

	/**
	 * give the OpenTerminal launcher a chance to deny app privileges
	 */
	public boolean shouldOpen()
	{
		return this.app.shouldOpen();
	}
	
	/**
	 * make sure that all System properties are set before calling this. In memory properties override other properties
	 */
	public Process launch(boolean open)
	{
        String libs = System.getProperty("java.class.path");
        if(JavaUtil.containsAny(libs, OpenTerminalConstants.INVALID))
        	throw new RuntimeException("one or more LIBRARIES contains illegal parsing characters:(" + libs + "), invalid:" + OpenTerminalConstants.INVALID);
        
		this.app.toProperties();
        ExeBuilder builder = new ExeBuilder();
    	builder.addCommand("java");
    	List<String> jvm = JavaUtil.asArray(this.app.jvmArgs);
    	builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.launchStage, OpenTerminalConstants.wrapping));//always use wrapper due to character limit on the command line
      	builder.addCommand(OpenTerminalUtil.writeDirProperty(jvm, "java.io.tmpdir"));
    	builder.addCommand(OpenTerminalUtil.writeDirProperty(jvm, "user.home"));
    	builder.addCommand(OpenTerminalUtil.writeDirProperty(jvm, "user.dir"));
    	builder.addCommand(OpenTerminalUtil.writeDirProperty(jvm, "user.appdata"));
    	this.app.writeProperties(jvm, builder);
    	builder.addCommand(jvm);
    	builder.addCommand("-cp");
    	String q = OSUtil.getQuote();
    	builder.addCommand(q + libs + q);
    	builder.addCommand(this.app.hardPause ? OpenTerminalWrapper.class.getName() : this.app.mainClass.getName());
    	builder.addCommand(OpenTerminalUtil.wrapProgramArgs(this.app.programArgs));
    	String command = builder.toString();
    	try
    	{
    		return open ? OpenTerminalUtil.runInNewTerminal(this.app.getAppdata(), this.app.terminal, this.app.name, this.app.shName, command) : OpenTerminalUtil.runInTerminal(this.app.terminal, command);
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
	public void relaunch(File reboot) 
	{
		System.out.println("re-launching");
		this.app = TerminalApp.fromFile(reboot);
		reboot.delete();
		this.app.process = this.launch(this.app.shouldOpen());
	}
	
	public void exit(int code)
	{
		System.exit(code);
	}

}
