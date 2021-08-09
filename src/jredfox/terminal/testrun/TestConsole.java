package jredfox.terminal.testrun;

import java.io.IOException;
import java.util.UUID;

import jredfox.common.utils.JREUtil;
import jredfox.terminal.OpenTerminal;
import jredfox.terminal.OpenTerminalConstants;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		if(OpenTerminal.INSTANCE != null);
		System.out.println(System.getProperty(OpenTerminalConstants.launchStage) + ":" + JREUtil.getJVMArgs() + "app:" + new TestConsole().newApp(args).jvmArgs);
		
		TerminalApp app = OpenTerminal.INSTANCE.run(TestConsole.class, args);
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
//		TerminalApp app = new TerminalAppWrapper("input thy args:", TestConsole.class, "test_app", UUID.randomUUID().toString(), "1.0.0", args).enableHardPause();
		return new TerminalApp(TestConsole.class, "test_app", UUID.randomUUID().toString(), "1.0.0", args).enableHardPause();
	}
	
}
