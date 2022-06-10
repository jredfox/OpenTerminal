package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;
import jml.ot.colors.AnsiColors;
import jml.ot.colors.AnsiColors.TermColors;
import jredfox.common.io.IOUtils;

/**
 * the terminal handler.{@link TerminalExe#run()} will give you a new UI from the executing terminal 
 * while {@link TerminalExe#getBootCmd()} is for when there already is a UI running and you simply want to boot the app
 */
public abstract class TerminalExe {
	
	public TerminalApp app;
	public File shell;
	
	public TerminalExe(TerminalApp app, File shell)
	{
		this.app = app;
		this.shell = shell;
	}
	/**
	 * your boot shell script
	 */
	public abstract void createShell() throws IOException;
	/**
	 * your starting scripts such as the start command for powershell or macOs
	 */
	public abstract void genStart() throws IOException;//ensures preset scripts like powershell's start or mac's start script and more are done before execution
	/**
	 * execute the command in a new terminal window
	 */
	public abstract void run();
	/**
	 * get's the boot command used by a console host instead of forcing a new window through other means
	 */
	public abstract List<String> getBootCmd();
	
	/**
	 * run the process builder with genStart and the boot shell checks
	 */
	public void run(ProcessBuilder pb)
	{
		try
		{
			this.genStart();
			this.createShell();
			this.printPB(pb);
			pb.directory(OTConstants.userDir).inheritIO().start();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			this.cleanup();
		}
	}
	
	/**
	 * delete your shells and startup scripts here. this happens when the applications fails to boot
	 */
	public abstract void cleanup();
	
	/**
	 * return a concated version of all color modes ending with the highest supported
	 */
	public String getColors(Profile p)
	{
		if(p != null)
		{
			String ansi4bit = AnsiColors.formatColor(TermColors.ANSI4BIT, p.bg, p.fg, p.ansiEsc);
			String xterm256 = AnsiColors.formatColor(TermColors.XTERM_256, p.bg, p.fg, p.ansiEsc);
			String truecolor = AnsiColors.formatColor(TermColors.TRUE_COLOR, p.bg, p.fg, p.ansiEsc);
			
			//TODO: ICP change based on terminal's $COLORTERM
			AnsiColors.setColorMode(this.app.ANSI4BIT ? TermColors.ANSI4BIT : TerminalUtil.windows_terminals.contains(this.app.terminal) ? TermColors.TRUE_COLOR : TermColors.XTERM_256);
			AnsiColors.setReset(AnsiColors.colorMode == TermColors.ANSI4BIT ? ansi4bit : AnsiColors.colorMode == TermColors.XTERM_256 ? xterm256 : AnsiColors.colorMode == TermColors.TRUE_COLOR ? truecolor : null); 
			return ansi4bit + xterm256 + truecolor;
		}
		return "";
	}
	
	public void makeShell(List<String> li) throws IOException 
	{
		this.makeShell(li, this.shell);
	}
	
	public void makeShell(List<String> li, File sh) throws IOException 
	{
		IOUtils.saveFileLines(li, sh, true);
		IOUtils.makeExe(sh);
	}
	
	public void printPB(ProcessBuilder pb)
	{
		for (String s : pb.command())
			System.out.print(s + " ");
		System.out.println("\b");
	}
}
