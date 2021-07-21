package jredfox.terminal.app;

/**
 * TerminalApp but, with wrapped arguments for command line strict jars which don't handle 0 arguments :)
 */
public class TerminalWrappedApp extends TerminalApp{

	public boolean wrapArgs;
	public String wrappedMsg = "";
	
	public TerminalWrappedApp(String id, String name, String version, Class<?> clazz, String[] args, boolean runDeob, boolean pause) 
	{
		super(id, name, version, clazz, args, runDeob, pause);
	}
	
	/**
	 * is your TerminalApp going to be grabbing arguments from the user before executing the jar?
	 */
	public boolean shouldWrapArgs()
	{
		return this.wrapArgs;
	}
	
	public String wrappedMsg()
	{
		return this.wrappedMsg();
	}
	
	public String[] wrappArgs()
	{
		return null;//TODO:
	}

}
