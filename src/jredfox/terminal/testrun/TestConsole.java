package jredfox.terminal.testrun;

import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.TerminalApp;

public class TestConsole {
	
	public static void main(String[] args)
	{
		TerminalApp app = new TerminalApp("test_app", "Test App", "1.0.0", args);
		OpenTerminal.INSTANCE.run(app);
//		System.setProperty("jredfox.property", "null");
	}

}
