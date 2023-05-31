package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;

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
					+ "  f=true\n"
					+ "else\n"
					+ "  printf \"$2\"\n"
					+ "  clear && printf \"\\033[3J\" #clears screen\n"
					+ "  f=false\n"
					+ "fi\n"
					+ "if ! [ -z \"$3\" ]\n"
					+ "then\n"
					+ "     echo -n -e \"\\033]0;\"$3\"\\007\" #changes the title if and only if the variable exists\n"
					+ "fi\n"
					+ "cd \"$4\"\n"
					+ "eval \"$5\"\n"
					+ "#start advanced pause method\n"
					+ "if [ \"$6\" = true ]; then\n"
					+ "  if [ \"$f\" = true ]; then\n"
					+ "      pmsg=\"$7\" #hd pause\n"
					+ "  else\n"
					+ "      pmsg=\"$8\" #xterm-256 or ansi4bit pause\n"
					+ "  fi\n"
					+ "  if [ -z \"$pmsg\" ]\n"
					+ "  then\n"
					+ "      pmsg=\"Press ENTER to continue...\"\n"
					+ "  fi\n"
					+ "  printf \"$pmsg\" #print pause\n"
					+ "  read -p \"\" #do the actual pause with no print due to CLI issues\n"
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
		String hd = p == null ? "" : p.getPauseMsg();
		String lowRes = p == null ? "" : p.getPauseLowResMsg();
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		if(this.app.terminal.equals("tilix"))
			command = command.replace("\\$", "\\\\$");//appears to want \\$ instead of just \$
		else if(this.quoteCmd)
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
			String.valueOf(this.app.pause),
			hd,
			lowRes
		});
		this.run(pb);
	}

	@Override
	public List<String> getBootCmd() 
	{
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replace("$", "\\$");
		Profile p = this.app.getProfile();
		String hd = p == null ? "" : p.getPauseMsg();
		String lowRes = p == null ? "" : p.getPauseLowResMsg();
		String trueColor = this.app.getBootTrueColor(p);
		String platteColor = this.app.getBootPaletteColor(p);
		List<String> li = new ArrayList<>();
		li.add("bash");
		li.add(this.shell.getPath());
		li.add(trueColor);
		li.add(platteColor);
		li.add(this.app.getTitle());
		li.add(OTConstants.userDir.getPath());
		li.add(command);
		li.add(String.valueOf(this.app.pause));
		li.add(hd);
		li.add(lowRes);
		return li;
	}

	@Override
	public void cleanup() 
	{
		this.shell.delete();
	}

}
