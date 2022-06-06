package jml.ot.terminal;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;

import jml.ot.TerminalApp;

public class TildaTerminalExe extends LinuxCmdTerminalExe{

	public TildaTerminalExe(TerminalApp app) throws IOException
	{
		super(app);
	}
	
	@Override
	public void run() throws IOException
	{
		super.run();
		try
		{
			//make tilda visible
			Thread.sleep(150);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_F1);
			robot.keyRelease(KeyEvent.VK_F1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
