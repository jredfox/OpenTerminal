package jredfox.terminal.testrun;

import java.io.IOException;
import java.util.UUID;

import jredfox.common.utils.JavaUtil;
import jredfox.terminal.OpenTerminal;
import jredfox.terminal.app.ITerminalApp;
import jredfox.terminal.app.TerminalApp;

public class TestConsole implements ITerminalApp {
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("\u2728\u02dc\u201d*\u00b0\u2022\ud835\ude83\ud835\ude8a\ud835\ude9c\ud835\ude9d\ud835\udea2_\ud835\ude71\ud835\ude92\ud835\ude9c\ud835\ude8c\ud835\ude9e\ud835\ude92\ud835\ude9d\u2022\u00b0*\u201d\u02dc\u2728#6161");
//		TerminalApp app = OpenTerminal.INSTANCE.run(TestConsole.class, args);
	}

	@Override
	public TerminalApp newApp(String[] args)
	{
//		TerminalApp app = new TerminalAppWrapper("input thy args:", TestConsole.class, "test_app", UUID.randomUUID().toString(), "1.0.0", args).enableHardPause();
		return new TerminalApp(TestConsole.class, "test_app", UUID.randomUUID().toString(), "1.0.0", args).enableHardPause();
	}
	
}
