package jml.ot.terminal;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import jml.ot.TerminalApp;

public class TildaTerminalExe extends LinuxCmdTerminalExe {

	public TildaTerminalExe(TerminalApp app)
	{
		super(app);
	}
	
	@Override
	public void run()
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
