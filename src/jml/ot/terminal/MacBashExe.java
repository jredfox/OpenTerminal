package jml.ot.terminal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jml.ot.TerminalUtil;
import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jredfox.common.io.IOUtils;

public class MacBashExe extends TerminalExe {
	
	public static final File macStart = new File(OTConstants.start, "mac");
	public static final File closeMeAs = new File(macStart, "closeMe.applescript");
	public static final File importAs = new File(macStart, "import.applescript");
	public static final File profileAs = new File(macStart, "profile.applescript");
	public static final File start2As = new File(macStart, "start2.applescript");
	public static final File closeMeScpt = new File(macStart, "closeMe.scpt");
	public static final File importScpt = new File(macStart, "import.scpt");
	public static final File profileScpt = new File(macStart, "profile.scpt");
	public static final File start2Scpt = new File(macStart, "start2.scpt");
	public static final File profileMac = new File(OTConstants.profiles, "mac");

	public MacBashExe(TerminalApp app) 
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
					+ "if [ \"$4\" = true ]; then\n"
					+ "    read -p \"Press ENTER to continue...\"\n"
					+ "fi\n"
					+ "c=\"closeMe_ ${6}\"\n"
					+ "echo -n -e \"\\033]0;\"$c\"\\007\"\n"
					+ "osascript \"$5\" \"$c\" & exit");
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
					+ "		activate\n"
					+ "		delay 1.0E-4\n"
					+ "		set winList to windows\n"
					+ "		repeat with win in winList\n"
					+ "			try\n"
					+ "				if name of win contains closeMe then\n"
					+ "					set processList to processes of win\n"
					+ "					set clean commands of current settings of win to processList\n"
					+ "					delay 1.0E-4\n"
					+ "					close win\n"
					+ "					if exists win then\n"
					+ "						activate --ensure terminal app is focused\n"
					+ "						tell application \"System Events\" to keystroke return --hit the return key in case of failure\n"
					+ "					end if\n"
					+ "				end if\n"
					+ "			end try\n"
					+ "		end repeat\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(li, closeMeAs, closeMeScpt);
		}
		if(!importScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set importScript to first item of argv\n"
					+ "	set profileId to second item of argv\n"
					+ "	set closeScript to third item of argv\n"
					+ "	do shell script \"open -a Terminal \" & importScript\n"
					+ "	do shell script \"osascript \" & closeScript & \" oti.\" & profileId & \".profile\"\n"
					+ "end run");
			this.makeAs(li, importAs, importScpt);
		}
		if(!profileScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set profileName to first item in argv\n"
					+ "	set profileId to \"_pf\" & profileName & \"_\"\n"
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
					+ "	set scpt to first item in argv\n"
					+ "	set p to second item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		if not application \"Terminal\" is running then launch\n"
					+ "		set newTab to do script scpt\n"
					+ "		try\n"
					+ "			if p is not equal to \"\" then set current settings of newTab to settings set p\n"
					+ "		end try\n"
					+ "		activate\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(li, start2As, start2Scpt);
		}
		
		//import the profile
		importProfile(this.app.getProfile());
	}

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

	@Override
	public void run() 
	{
		String q = TerminalUtil.getQuote();
		String profile = this.app.getProfile() != null && this.app.getProfile().mac_profileName != null ? this.app.getProfile().mac_profileName : "";
		String command = (OTConstants.java_home + " " + OTConstants.args).replaceAll(q, "\\\\" + q);
		String bash = "bash " + q + this.shell.getPath() + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + this.app.pause + q + " " + q + closeMeScpt.getPath() + q + " " + q + System.currentTimeMillis() + q;
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"osascript",
			start2Scpt.getPath(),
			bash,
			profile
		});
		this.run(pb);
	}

	@Override
	public List<String> getBootCmd() 
	{
		String q = TerminalUtil.getQuote();
		String command = (OTConstants.java_home + " " + OTConstants.args).replaceAll(q, "\\\\" + q);
		String bash = q + this.shell.getPath() + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + this.app.pause + q + " " + q + closeMeScpt.getPath() + q + " " + q + System.currentTimeMillis() + q;
		List<String> li = new ArrayList<>();
		li.add("bash");
		li.add("-c");
		li.add(bash);
		return li;
	}
	
	public static void importProfile(Profile p) throws IOException
	{
		String q = TerminalUtil.getQuote();
		if(p != null && p.mac_profileId != null)
		{
			File pf = new File(profileMac, p.mac_profileId + ".terminal");
			if(!pf.exists())
			{
				System.out.println("importing Terminal.app profile " + p.mac_profileId);
				
				//copy the profile.terminal to the disk
				InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(p.mac_profilePath);
				IOUtils.makeParentDirs(pf);
				IOUtils.copy(in, new BufferedOutputStream(new FileOutputStream(pf)));
				
				//import the profile.terminal
				ProcessBuilder pb = new ProcessBuilder(new String[] 
				{
					"osascript",
					importScpt.getPath(),
					q + pf.getPath() + q,
					q + p.mac_profileId + q,
					q + closeMeScpt.getPath() + q
				}).directory(OTConstants.userDir).inheritIO();
				Process process = pb.start();
				while(process.isAlive());
			}
		}
	}

	@Override
	public void cleanup()
	{
		this.shell.delete();
		IOUtils.deleteDirectory(macStart);
	}

}
