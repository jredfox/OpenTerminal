package jredfox.terminal.testrun;

import java.io.IOException;
import java.util.UUID;

import jredfox.common.utils.JREUtil;
import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;
import jredfox.terminal.app.TerminalAppWrapper;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		TerminalAppWrapper app = (TerminalAppWrapper) OpenTerminal.INSTANCE.run(TestConsole.class, args);
//		app.name = "" + System.currentTimeMillis();
		System.out.println(args.length);
		JREUtil.sleep(2500);
		app.reboot();
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
		return new TerminalAppWrapper("input thy args:", TestConsole.class, "test_app", UUID.randomUUID().toString(), "1.0.0", args).enableHardPause();
	}
	
}
