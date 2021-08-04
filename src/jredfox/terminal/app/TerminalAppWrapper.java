package jredfox.terminal.app;

/**
 * TerminalApp but, with wrapped arguments for command line strict jars which don't handle 0 arguments :)
 */
public class TerminalAppWrapper extends TerminalApp {

	public boolean wrapArgs;
	public String wrappedMsg = "";
	
	public TerminalAppWrapper()
	{
		
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

	public String[] getWrappedArgs(String[] args) 
	{
		return null;//TODO:
	}

}
