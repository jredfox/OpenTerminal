package jml.ot.app.host;

import java.io.IOException;

import jml.ot.TerminalApp;

public abstract class ConsoleHost {
	
	public TerminalApp app;
	
	public ConsoleHost(TerminalApp app)
	{
		this.app = app;
	}
	
	public abstract void run() throws IOException;

}
