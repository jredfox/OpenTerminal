package jml.ot.terminal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import jml.ot.OTConstants;
import jml.ot.TerminalApp;
import jml.ot.TerminalApp.Profile;
import jml.ot.TerminalUtil;
import jml.ot.colors.AnsiColors;
import jredfox.common.io.IOUtils;

public class MacBashExe extends TerminalExe {
	
	public static final File macStart = new File(OTConstants.start, "mac");
	public static final File closeMeAs = new File(macStart, "closeMe.applescript");
	public static final File profileAs = new File(macStart, "profile.applescript");
	public static final File startAs = new File(macStart, "start.applescript");
	public static final File getProfileAs = new File(macStart, "getProfile.applescript");
	
	public static final File closeMeScpt = new File(macStart, "closeMe.scpt");
	public static final File profileScpt = new File(macStart, "profile.scpt");
	public static final File getProfileScpt = new File(macStart, "getProfile.scpt");
	public static final File startScpt = new File(macStart, "start.scpt");
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
					+ "clear && printf \"\\033[3J\" #clears screen\n"
					+ "t=\"$TERM$COLORTERM\" #set the color format in bash to update the UI as it's faster then waiting for IPC connection should prevent less flickering\n"
					+ "if [[ $t == *\"true\"* && $t == *\"color\"* ]] || [[ $t == *\"24\"* && $t == *\"bit\"* ]]; then\n"
					+ "  printf \"$1\"\n"
					+ "  clear && printf \"\\033[3J\" #clears screen\n"
					+ "  f=true\n"
					+ "  color=\"$1\"\n"
					+ "else\n"
					+ "  printf \"$2\"\n"
					+ "  clear && printf \"\\033[3J\" #clears screen\n"
					+ "  f=false\n"
					+ "  color=\"$2\"\n"
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
					+ "  printf \"$pmsg$color\" #print pause\n"
					+ "  read -p \"\" #do the actual pause with no print due to CLI issues\n"
					+ "fi\n"
					+ "c=\"closeMe_ $9\"\n"
					+ "echo -n -e \"\\033]0;\"$c\"\\007\"\n"
					+ "osascript \"${10}\" \"$c\" & exit");
			this.makeShell(li);
		}
	}
	
	@Override
	public void genStart() throws IOException 
	{
		long ms = System.currentTimeMillis();
		List<Process> processes = new ArrayList<>(5);
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
			this.makeAs(processes, li, closeMeAs, closeMeScpt);
		}
		if(!profileScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set p to first item in argv\n"
					+ "	set lookFor to second item in argv\n"
					+ "	set hasFound to false\n"
					+ "	tell application \"Terminal\"\n"
					+ "		--find the selected tab\n"
					+ "		set winList to windows\n"
					+ "		repeat with win in winList\n"
					+ "			set n to name of win\n"
					+ "			if name of win contains lookFor then\n"
					+ "				set newWin to win\n"
					+ "				set hasFound to true\n"
					+ "				exit repeat\n"
					+ "			end if\n"
					+ "		end repeat\n"
					+ "		--set the profile and import\n"
					+ "		try\n"
					+ "			if hasFound then set current settings of newWin to settings set p\n"
					+ "		on error\n"
					+ "			--start the import if it doesn't exist\n"
					+ "			set otHome to third item in argv\n"
					+ "			set custom title of newWin to \"\" --prevent it from being accidently closed\n"
					+ "			set pscpt to otHome & \"/profiles/mac/\" & p & \".terminal\"\n"
					+ "			set cscpt to otHome & \"/scripts/start/mac/closeMe.scpt\"\n"
					+ "			do shell script \"open -a Terminal \\\"\" & pscpt & \"\\\"\"\n"
					+ "			do shell script \"osascript \\\"\" & cscpt & \"\\\"\" & \" oti.\" & p & \".profile\"\n"
					+ "			try\n"
					+ "				set current settings of newWin to settings set p --try to set the profile after importing it\n"
					+ "				set custom title of settings set p to \"\" --after importing it change profile's title window to blank\n"
					+ "			end try\n"
					+ "		end try\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(processes, li, profileAs, profileScpt);
		}
		if(!getProfileScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set lookFor to first item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		repeat with win in windows\n"
					+ "			if name of win contains lookFor then\n"
					+ "				set a to name of current settings of win\n"
					+ "				log a\n"
					+ "				exit repeat\n"
					+ "			end if\n"
					+ "		end repeat\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(processes, li, getProfileAs, getProfileScpt);
		}
		if(!startScpt.exists())
		{
			List<String> li = new ArrayList<>();
			li.add("on run argv\n"
					+ "	set scpt to first item in argv\n"
					+ "	set p to second item in argv\n"
					+ "	tell application \"Terminal\"\n"
					+ "		if not application \"Terminal\" is running then launch\n"
					+ "		set newTab to do script \"\"\n"
					+ "		try\n"
					+ "			if p is not equal to \"\" then set current settings of newTab to settings set p\n"
					+ "		on error --import the profile when it's not currently imported\n"
					+ "			set otHome to third item in argv\n"
					+ "			set custom title of newTab to \"\" --prevent it from being accidently closed\n"
					+ "			set pscpt to otHome & \"/profiles/mac/\" & p & \".terminal\"\n"
					+ "			set cscpt to otHome & \"/scripts/start/mac/closeMe.scpt\"\n"
					+ "			do shell script \"open -a Terminal \\\"\" & pscpt & \"\\\"\"\n"
					+ "			do shell script \"osascript \\\"\" & cscpt & \"\\\"\" & \" oti.\" & p & \".profile\"\n"
					+ "			try\n"
					+ "				if p is not equal to \"\" then set current settings of newTab to settings set p --try to set the profile after importing it\n"
					+ "				set custom title of settings set p to \"\" --after importing it change profile's title window to blank\n"
					+ "			end try\n"
					+ "		end try\n"
					+ "		do script scpt in newTab --do the actual boot script\n"
					+ "		activate\n"
					+ "	end tell\n"
					+ "end run");
			this.makeAs(processes, li, startAs, startScpt);
		}
		
		//import the profile while compiling and logging to boot if possible
		importProfile(this.app.getProfile(), this.app.canLogBoot ? this.app.bootLogger : System.out);

		if(!processes.isEmpty())
		{
			//wait for all the compiles to be done
			for(Process p : processes)
			{
				try 
				{
					p.waitFor();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
					while(p.isAlive());//continue to wait since Process#waitFor() can cause exceptions
				}
			}
			//make the scripts runnable
			IOUtils.makeExe(closeMeScpt);
			IOUtils.makeExe(profileScpt);
			IOUtils.makeExe(getProfileScpt);
			IOUtils.makeExe(startScpt);
		}
		
		long time = System.currentTimeMillis()-ms;
		this.app.logBoot("AppleScript & Import Profile File Time:" + time + "ms");
	}
	
	public void makeAs(List<Process> processes, List<String> commands, File applescript, File scpt) throws IOException
	{
		this.makeShell(commands, applescript);
		processes.add(this.noSyncCompileAS(applescript, scpt));
	}
	
	public void makeAs(List<String> commands, File applescript, File scpt) throws IOException
	{
		this.makeShell(commands, applescript);
		compileAS(applescript, scpt);
	}

	public Process noSyncCompileAS(File as, File scpt) throws IOException
	{
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"osacompile",
			"-o",
			scpt.getPath(),
			as.getPath()
		}).directory(OTConstants.userDir);
		Process p = pb.start();
		return p;
	}
	
	public static void compileAS(File as, File scpt) throws IOException
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
		Profile p = this.app.getProfile();
		String hd = p == null ? "" : p.getPauseMsg().replace(AnsiColors.ESC, "\\033");
		String lowres = p == null ? "" : p.getPauseLowResMsg().replace(AnsiColors.ESC, "\\033");
		String profileId = p == null ? "" : p.mac_profileName;
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replaceAll(q, "\\\\" + q).replace("$", "\\\\\"\"$\"\"");
		String trueColor = app.getBootTrueColor(p).replace(AnsiColors.ESC, "\\033");
		String platteColor = app.getBootPaletteColor(p).replace(AnsiColors.ESC, "\\033");
		String bash = "bash " + q + this.shell.getPath() + q + " " + q + trueColor + q + " " + q + platteColor + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + this.app.pause + q + " " + q + hd + q + " " + q + lowres + q + " " + q + this.app.sessionName + q + " " + q + closeMeScpt.getPath() + q;
		ProcessBuilder pb = new ProcessBuilder(new String[]
		{
			"osascript",
			startScpt.getPath(),
			bash,
			profileId,
			OTConstants.home.getPath()
		});
		this.run(pb);
	}
	
	@Override
	public List<String> getBootCmd() 
	{
		String q = TerminalUtil.getQuote();
		Profile p = this.app.getProfile();
		String hd = p == null ? "" : p.getPauseMsg().replace(AnsiColors.ESC, "\\033");
		String lowres = p == null ? "" : p.getPauseLowResMsg().replace(AnsiColors.ESC, "\\033");
		String command = (OTConstants.java_home + " " + this.getJVMFlags() + " " + OTConstants.args).replaceAll(q, "\\\\" + q).replace("$", "\\\\\"\"$\"\"");
		String trueColor = this.app.getBootTrueColor(p).replace(AnsiColors.ESC, "\\033");
		String platteColor = this.app.getBootPaletteColor(p).replace(AnsiColors.ESC, "\\033");
		String bash = "bash " + q + this.shell.getPath() + q + " " + q + trueColor + q + " " + q + platteColor + q + " " + q + this.app.getTitle() + q + " " + q + OTConstants.userDir + q + " " + q + command + q + " " + q + this.app.pause + q + " " + q + hd + q + " " + q + lowres + q + " " + q + this.app.sessionName + q + " " + q + closeMeScpt.getPath() + q;
		List<String> li = new ArrayList<>();
		li.add("bash");
		li.add(bash);
		return li;
	}
	
	@Override
	public void applyProperties() throws IOException, InterruptedException
	{
		Profile p = this.app.getProfile();
		if(p != null && p.mac_profileName != null)
		{
			this.genStart();
			//set the title to the sessions name so the scripts can find this window
			System.out.print("]0;" + this.app.sessionName + "");
			System.out.flush();
			
			//get the profile's name
			ProcessBuilder getName = new ProcessBuilder(new String[] {"osascript", MacBashExe.getProfileScpt.getPath(), this.app.sessionName});
			Process pr = getName.start();
			pr.waitFor();
			List<String> arr = IOUtils.getFileLines(IOUtils.getReader(pr.getErrorStream()));
			if(arr.isEmpty())
				arr = IOUtils.getFileLines(IOUtils.getReader(pr.getInputStream()));
			this.app.defaultProfile = arr.get(0);
			
			//set the current profile
			ProcessBuilder pb = new ProcessBuilder(new String[] {"osascript", MacBashExe.profileScpt.getPath(), p.mac_profileName, this.app.sessionName, OTConstants.home.getPath()});
			pb.start().waitFor();
		}
		super.applyProperties();
	}
	
	@Override
	public void applyDefaultProperties()
	{
		if(this.app.defaultProfile != null)
		{
			try
			{
				System.out.print("]0;" + this.app.sessionName + "");//clear the title so the CLI resets itself
				System.out.flush();
				System.out.println(this.app.defaultProfile + " " + this.app.sessionName);
				ProcessBuilder pb = new ProcessBuilder(new String[] {"osascript", MacBashExe.profileScpt.getPath(), this.app.defaultProfile, this.app.sessionName, OTConstants.home.getPath()});
				pb.start().waitFor();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		super.applyDefaultProperties();
	}
	
	/**
	 * imports the profile from from the jar to the correct location
	 */
	public static void importProfile(Profile p, PrintStream out) throws IOException
	{
		if(p != null && p.mac_profileId != null)
		{
			File pf = new File(profileMac, p.mac_profileId + ".terminal");
			if(!pf.exists())
			{
				out.println("extracting Terminal.app profile " + p.mac_profileId + " to:" + pf);
				
				//copy the profile.terminal to the disk
				InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(p.mac_profilePath);
				IOUtils.makeParentDirs(pf);
				IOUtils.copy(in, new BufferedOutputStream(new FileOutputStream(pf)));
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
