package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.TerminalApp;

public class TestConsole {
	
	public static void main(String[] args) throws IOException
	{
		TerminalApp app = new TerminalApp("test_app", "Test App", "1.0.0", args).enableHardPause();
		OpenTerminal.INSTANCE.run(app);
		app.name = "" + System.currentTimeMillis();
		app.reboot();
	}

}
