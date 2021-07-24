package jredfox.terminal;

import java.io.File;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.os.OSUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;
import jredfox.terminal.app.TerminalAppWrapped;

public class OpenTerminal {
	
	public static OpenTerminal INSTANCE = new OpenTerminal();
	public TerminalApp app;
	public boolean canReboot = true;
	
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
		if(this.app == null)
			throw new IllegalArgumentException("TerminalApp cannot be null!");
		else if(System.getProperty(OpenTerminalConstants.launchStage).equals(OpenTerminalConstants.exe))
			return;
		else if(System.getProperty(OpenTerminalConstants.launchStage).equals(OpenTerminalConstants.wrapping))
		{
			System.setProperty(OpenTerminalConstants.launchStage, OpenTerminalConstants.exe);
			OpenTerminalWrapper.run(this.app, this.app.getProgramArgs());
			return;//return as the TerminalApp is done executing and System#exit has already been called on the child process
		}
		
		this.app.process = this.launch(this.shouldOpen());
		JREUtil.sleep(700);
		int exit = this.app.process != null ? 0 : -1;
		while(this.app.process != null)
		{
			if(!this.app.process.isAlive())
			{
				exit = this.app.process.exitValue();
				File reboot = new File(this.app.getAppdata(), "reboot.properties");
//				System.out.println(reboot.exists());
				this.app.process = this.canReboot && reboot.exists() ? this.relaunch(reboot) : null;
				JREUtil.sleep(500);
			}
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
    	builder.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.launchStage, this.app.shouldPause || this.app.hardPause || this.app instanceof TerminalAppWrapped ? OpenTerminalConstants.wrapping : OpenTerminalConstants.exe));//go to exe if no wrapper is used / required
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
	public Process relaunch(File reboot) 
	{
		System.out.println("re-launching");
		TerminalApp app = null;
		try
		{
			TerminalApp.parseProperties(reboot);
			app = (TerminalApp) JREUtil.getClass(System.getProperty("openterminal.TerminalAppClass"), true).newInstance();
			app.fromProperties();
			app.idHash = "" + System.currentTimeMillis();
			app.jvmArgs.add(System.getProperty(OpenTerminalConstants.jvmArgs));
			app.programArgs.add(System.getProperty(OpenTerminalConstants.programArgs));
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		this.app = app;
		return this.launch(this.app.shouldOpen());
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
