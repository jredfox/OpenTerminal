package jml.ipc.pipes;

import jml.ot.OpenTerminal;
import jml.ot.TerminalApp;

public class TestOpenArgs {
	
	public static void main(String[] args)
	{
		args = OpenTerminal.openWithArgs(new TerminalApp("test_open_args", "TEST OPEN ARGS", ""), "Enter A Command:", args);
		int i = 0;
		for(String s : args)
			System.out.println("arg" + i++ + ":" + s);
		System.exit(0);
	}

}
