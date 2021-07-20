package jredfox.terminal.testrun;

import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.TerminalApp;

public class TestConsole {
	
	public static void main(String[] args)
	{
		TerminalApp app = new TerminalApp(args);
		OpenTerminal.INSTANCE.run(app);
	}

}
