package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.TerminalUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;

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
					+ "clear && printf '\\e[3J' #clears screen\n"
					+ "if ! [ -z \"$1\" ]\n"
					+ "then\n"
					+ "     echo -n -e \"\\033]0;\"$1\"\\007\" #changes the title if and only if the variable exists\n"
					+ "fi\n"
					+ "cd \"$2\"\n"
					+ "eval \"$3\"\n"
					+ "if [ \"$4\" = true ]; then\n"
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
		String command = (OTConstants.java_home + " " + OTConstants.args);
		if(this.quoteCmd)
			command = command.replaceAll(TerminalUtil.getQuote(),"\\\\" + TerminalUtil.getQuote());
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			this.app.terminal,
			this.app.getLinuxExe(),
			"bash",
			this.shell.getPath(),
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
		String command = (OTConstants.java_home + " " + OTConstants.args);
		List<String> li = new ArrayList<>();
		li.add("bash");
		li.add(this.shell.getPath());
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

}
