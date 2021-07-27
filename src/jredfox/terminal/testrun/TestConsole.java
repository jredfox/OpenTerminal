package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.common.utils.JavaUtil;

public class TestConsole {
	
	public static void main(String[] args) throws IOException
	{
		System.out.println(JavaUtil.toUnicodeEsq("&#916;") + " \u0394");
//		TerminalApp app = new TerminalApp("test_app", "Test App", "1.0.0", args).enableHardPause();
//		OpenTerminal.INSTANCE.run(app);
//		app.name = "" + System.currentTimeMillis();
//		app.reboot();
	}

}
