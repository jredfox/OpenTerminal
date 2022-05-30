package jml.ot.terminal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OSUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jredfox.common.io.IOUtils;

public class MacBashExe extends TerminalExe {
	
	public static final File macStart = new File(OTConstants.start, "mac");
	public static final File closeMeAs = new File(macStart, "closeMe.applescript");
	public static final File importProfileAs = new File(macStart, "import.applescript");
	public static final File profileAs = new File(macStart, "profile.applescript");
	public static final File start2As = new File(macStart, "start2.applescript");
	public static final File closeMeScpt = new File(macStart, "closeMe.scpt");
	public static final File importProfileScpt = new File(macStart, "import.scpt");
	public static final File profileScpt = new File(macStart, "profile.scpt");
	public static final File start2Scpt = new File(macStart, "start2.scpt");

	public MacBashExe(TerminalApp app) throws IOException 
	{
		super(app, new File(OTConstants.boot, "macboot.sh"));
	}

	/**
	 * create the macOs boot script. I leave the option for a custom title but it's unused if you are using the start2.scpt as they have that in the args
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
					+ "echo -n -e \"\\033]0;_closeMe_\\007\"\n"
					+ "osascript \"$4\" \"_closeMe_\"");
			this.makeShell(li);
		}
	}
	
	@Override
	public void genStart() throws IOException 
	{
		if(!closeMeScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set closeMe to first item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		set winList to windows\n"
					+ "		repeat with win in winList\n"
					+ "			if name of win contains closeMe then\n"
					+ "				set processList to processes of win\n"
					+ "				set clean commands of current settings of win to processList\n"
					+ "				close win\n"
					+ "			end if\n"
					+ "		end repeat\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(li, closeMeAs, closeMeScpt);
		}
		if(!importProfileScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set importScript to first item of argv\n"
					+ "	set closeScript to second item of argv\n"
					+ "	set profileId to third item of argv\n"
					+ "	do shell script \"open -a Terminal \" & importScript\n"
					+ "	do shell script \"osascript \" & closeScript & \" _oti_\" & profileId & \"_\"\n"
					+ "end run");
			this.makeAs(li, importProfileAs, importProfileScpt);
		}
		if(!profileScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set profileId to \"_pf\" & first item in argv & \"_\"\n"
					+ "	set profileName to second item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		set current settings of (every window whose name contains profileId) to settings set profileName\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(li, profileAs, profileScpt);
		}
		if(!start2Scpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set flag to application \"Terminal\" is not running\n"
					+ "	set scpt to first item in argv\n"
					+ "	set n to second item in argv\n"
					+ "	set p to third item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		set newTab to do script scpt\n"
					+ "		try\n"
					+ "			if p is not equal to \"\" then set current settings of newTab to settings set p\n"
					+ "		end try\n"
					+ "		set custom title of newTab to n\n"
					+ "		activate\n"
					+ "		delay 0.1\n"
					+ "		set badFlag to back window is equal to window 1\n"
					+ "		if flag and (not badFlag) then\n"
					+ "			set bwindow to back window\n"
					+ "			set processList to processes of bwindow\n"
					+ "			set clean commands of current settings of bwindow to processList\n"
					+ "			close bwindow\n"
					+ "		end if\n"
					+ "	end tell\n"
					+ "end run\n"
					+ "");
			this.makeAs(li, start2As, start2Scpt);
		}
	}
	//osascript <start2.scpt> "bash <boot.sh> \"\" \"<cd>\" \"<command>\" \"<closeMe.scpt>\"" "<AppName>" "profileName"
	public void makeAs(List<String> commands, File applescript, File scpt) throws IOException
	{
		this.makeShell(commands, applescript);
		this.compileAS(applescript, scpt);
	}
	
	public void compileAS(File as, File scpt) throws IOException
	{
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"osacompile",
			"-o",
			scpt.getPath(),
			as.getPath()
		}).directory(OTConstants.userDir);
		Process p = pb.start();
		while(p.isAlive())
		{
			;
		}
		IOUtils.makeExe(scpt);
	}

	//TODO: test to see if not running osascript by itself without the terminal works on older macOs versions
	@Override
	public void run() throws IOException 
	{
		String q = OSUtil.getQuote();
		String profile = this.app.getProfile() != null ? this.app.getProfile().profileName : "";
		String command = (OTConstants.java_home + " " + OTConstants.args).replaceAll(q, "\\\\" + q);
		String bash = "bash " + q + this.shell.getPath() + q + " " + q + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + closeMeScpt.getPath() + q;
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"osascript",
			start2Scpt.getPath(),
			bash,
			this.app.getTitle(),
			profile
		});
		this.run(pb);
	}

	@Override
	public List<String> getBootCmd() throws IOException 
	{
		String q = OSUtil.getQuote();
		String profile = this.app.getProfile() != null ? this.app.getProfile().profileName : "";
		String command = (OTConstants.java_home + " " + OTConstants.args).replaceAll(q, "\\\\" + q);
		String bash = "bash " + q + this.shell.getPath() + q + " " + q + profile + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + closeMeScpt.getPath() + q;
		List<String> li = new ArrayList<>();
		li.add(bash);
		return li;
	}

}
