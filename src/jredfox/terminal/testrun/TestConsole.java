package jredfox.terminal.testrun;

import java.io.IOException;

import jredfox.common.utils.JavaUtil;

public class TestConsole {
	
	public static void main(String[] args) throws IOException
	{
		System.out.println(JavaUtil.getUnicodeEsq("abcd0123456789\u00a9$#!@~{}[]<>?/"));
		System.out.println(JavaUtil.getUnicodeEsq("\ud83e\uddf8abcd"));
//		TerminalApp app = new TerminalApp("test_app", "Test App", "1.0.0", args).enableHardPause();
//		OpenTerminal.INSTANCE.run(app);
//		app.name = "" + System.currentTimeMillis();
//		app.reboot();
	}

}
