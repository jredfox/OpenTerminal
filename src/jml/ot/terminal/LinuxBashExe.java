package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;
import jml.ot.colors.AnsiColors;

public class LinuxBashExe extends TerminalExe{

	public boolean quoteCmd;
	
	public LinuxBashExe(TerminalApp app) 
	{
		super(app, new File(OTConstants.boot, "linuxboot.sh"));
	}

	/**
	 * same as mac's bash minus the closeMe.applescript
	 */
	@Override
	public void createShell() throws IOException 
	{
		if(!this.shell.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("#!/bin/bash\n"
					+ "set +v #echo off\n"
					+ "clear && printf \"\\033[3J\" #clears screen\n"
					+ "t=\"$TERM$COLORTERM\" #set the color format in bash to update the UI as it's faster then waiting for IPC connection should prevent less flickering\n"
					+ "if [[ $t == *\"true\"* && $t == *\"color\"* ]] || [[ $t == *\"24\"* && $t == *\"bit\"* ]]; then\n"
					+ "  printf \"$1\"\n"
					+ "  clear && printf \"\\033[3J\" #clears screen\n"
					+ "else\n"
					+ "  printf \"$2\"\n"
					+ "  clear && printf \"\\033[3J\" #clears screen\n"
					+ "fi\n"
					+ "if ! [ -z \"$3\" ]\n"
					+ "then\n"
					+ "     echo -n -e \"\\033]0;\"$3\"\\007\" #changes the title if and only if the variable exists\n"
					+ "fi\n"
					+ "cd \"$4\"\n"
					+ "eval \"$5\"\n"
					+ "if [ \"$6\" = true ]; then\n"
					+ "    read -p \"Press ENTER to continue...\"\n"
					+ "fi\n"
					+ "exit");
			this.makeShell(li);
		}
	}

	@Override
	public void genStart() throws IOException 
	{
		
	}

	@Override
	public void run() 
	{
		Profile p = this.app.getProfile();
//		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		if(this.app.terminal.equals("tilix"))
			command = command.replace("\\$", "\\\\$");//appears to want \\$ instead of just \$
		if(this.quoteCmd)
		{
			//only used for terminalpp
			command = command.replace("\\$", "\"\"$\"\"");
			command = command.replaceAll(TerminalUtil.getQuote(),"\\\\" + TerminalUtil.getQuote()).replace("$", "\\\\$");
		}
		String trueColor = this.app.getBootTrueColor(p);
		String platteColor = this.app.getBootPaletteColor(p);
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			this.app.terminal,
			this.app.getLinuxExe(),
			"bash",
			this.shell.getPath(),
			trueColor,
			platteColor,
			this.app.getTitle(),
			OTConstants.userDir.getPath(),
			command,
			String.valueOf(this.app.pause)
		});
		this.run(pb);
	}

	@Override
	public List<String> getBootCmd() 
	{
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		Profile p = this.app.getProfile();
		String trueColor = this.app.getBootTrueColor(p).replace(AnsiColors.ESC, "\\033");
		String platteColor = this.app.getBootPaletteColor(p).replace(AnsiColors.ESC, "\\033");
		List<String> li = new ArrayList<>();
		li.add("bash");
		li.add(this.shell.getPath());
		li.add(trueColor);
		li.add(platteColor);
		li.add(this.app.getTitle());
		li.add(OTConstants.userDir.getPath());
		li.add(command);
		li.add(String.valueOf(this.app.pause));
		return li;
	}

	@Override
	public void cleanup() 
	{
		this.shell.delete();
	}
	
	@Override
	public String getJVMFlags()
	{
		return super.getJVMFlags().replace("@", "$");//TODO: fix after debug
	}

}
