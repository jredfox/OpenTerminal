package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.common.utils.JREUtil;
import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		TerminalApp app = OpenTerminal.INSTANCE.run(TestConsole.class, args);
		app.name = "" + System.currentTimeMillis();
//		JREUtil.sleep(4000);
//		app.reboot();
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
		return new TerminalApp(TestConsole.class, "test_app", "Test App", "1.0.0", args).enableHardPause();
	}
	
}
