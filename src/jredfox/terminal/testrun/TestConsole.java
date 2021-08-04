package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		TerminalApp app = new TerminalApp("test_app", "Test App", "1.0.0", TestConsole.class, args).enableHardPause();
		OpenTerminal.INSTANCE.run(app);
		app.name = "" + System.currentTimeMillis();
//		JREUtil.sleep(2000);
		app.reboot();
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
		return new TerminalApp("test_app", "Test App", "1.0.0", TestConsole.class, args).enableHardPause();
	}
	
}
