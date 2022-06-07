package jml.ot.terminal.host;

import jml.ot.TerminalApp;

/**
 * the Console Handler aka the UI for the terminal handler
 */
public abstract class ConsoleHost {
	
	public TerminalApp app;
	
	public ConsoleHost(TerminalApp app)
	{
		this.app = app;
	}
	
	public abstract void run();

}
