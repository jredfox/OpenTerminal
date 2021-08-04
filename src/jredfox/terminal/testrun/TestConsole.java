package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;
import jredfox.terminal.app.TerminalAppWrapper;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		TerminalAppWrapper app = (TerminalAppWrapper) OpenTerminal.INSTANCE.run(TestConsole.class, args);
		app.name = "" + System.currentTimeMillis();
//		JREUtil.sleep(2500);
//		app.reboot(false);
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
		return new TerminalAppWrapper("input thy args:", TestConsole.class, "test_app", "Test App", "1.0.0", args).enableHardPause();
	}
	
}
