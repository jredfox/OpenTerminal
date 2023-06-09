package jml.ot;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import jml.ipc.pipes.FilePipeManager;
import jml.ipc.pipes.PipeInputStream;
import jml.ipc.pipes.PipeManager;
import jml.ipc.pipes.WrappedPrintStream;
import jml.ot.colors.AnsiColors;
import jml.ot.colors.AnsiColors.TermColors;
import jml.ot.colors.ColoredPrintStream;
import jml.ot.colors.Palette;
import jml.ot.terminal.BatchExe;
import jml.ot.terminal.GuakeTerminalExe;
import jml.ot.terminal.LinuxBashExe;
import jml.ot.terminal.LinuxCmdTerminalExe;
import jml.ot.terminal.MacBashExe;
import jml.ot.terminal.PowerShellExe;
import jml.ot.terminal.TerminalExe;
import jml.ot.terminal.ValaTerminalExe;
import jml.ot.terminal.host.ConsoleHost;
import jml.ot.terminal.host.WTHost;
import jredfox.common.config.MapConfig;
import jredfox.common.file.FileUtils;
import jredfox.common.io.IOUtils;
import jredfox.common.utils.Assert;
import jredfox.common.utils.FileUtil;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;

public class TerminalApp {
	
	public String id;
	public String name;
	public String version;
	/**
	 * Opens a new CLI regardless of the initial boot. if you use multiple CLI's set this to true
	 */
	public boolean force;
	/**
	 * shellscript pause catches {@link System#exit(int)}
	 */
	public boolean pause;
	/***
	 * Java pause, doesn't catch {@link System#exit(int)}
	 */
	public boolean softPause;
	public String terminal = "";
	public String conHost = "";
	public List<String> linuxCmdsExe = new ArrayList<>(0);//configurable list to use LinuxCmdExe instead of LinuxBash or another
	public Map<String, String> linuxFlags = new HashMap<>(0);//override linux new window flags in case you have an updated or outdated version then is currently supported
	protected Profile profile;
	/**
	 * changing this to true will make you use 4 bit ANSI colors instead of xterm256 or true colors
	 */
	public boolean ANSI4BIT;
	/**
	 * the configurable palette string. resources/jml/ot/colors/*
	 */
	public String ansi4bitPalette = "";
	public AnsiColors colors = new AnsiColors();
	/**
	 * when enabled it loggs boot errors and sanity reasons. The boot log only loogs the boot debug and not your program
	 */
	public boolean canLogBoot = true;
	/**
	 * Logger during boot of {@link OpenTerminal#open(TerminalApp)}. It closes after boot has been completed
	 */
	public PrintStream bootLogger;
	/**
	 * wheather or not your TerminalApp should log. disabled by default in case users want to use jredfox logger or log4j
	 */
	public boolean shouldLog = false;
	/**
	 * the log file populated during {@link #loadSession()}
	 */
	public File logger;
	/**
	 * the IPC Pipe manager.
	 */
	public PipeManager manager;
	/**
	 * when true Console is nonnull on initial boot, PipeManager will be null, and if {@link #pause()} is true it will also do a java pause instead of a shell pause
	 */
	public boolean isShellDisabled;
	/**
	 * tell's PipeManager whether or not to replace the STD ERR & IN. Disable this if you have multiple CLI's running at once for your program
	 */
	public boolean replaceSYSO = true;
	/**
	 * the unique session for this terminal app to prevent IPC appdata collisions. autogenerated from {@link #load()}
	 */
	public File session;
	public String sessionName;
	/**
	 * set this to true to {@link System#exit(int)} on CLI client close
	 */
	public boolean exitOnCLI;
	/**
	 * Config option. When true if a terminal $COLORTERM$TERM string changes it will sync these changes live without reboot
	 */
	public boolean syncColorModeThread = true;
	/**
	 * populated from terminal.dat on {@link #loadConfig()}
	 */
	protected String cachedColorTerm;
	protected boolean ANSI4BitDatFlag = false;
	/**
	 * set this field if you require custom logic on the CLI client side running it must have a valid default contructor
	 */
	public Class<? extends TerminalApp> appClass = null;
	
	public TerminalApp(String id, String name, String version)
	{
		this(id, name, version, false);
	}
	
	public TerminalApp(String id, String n, String v, boolean force)
	{
		this(id, n, v, force, true);
	}
	
	public TerminalApp(String id, String name, String version, boolean force, boolean pause)
	{
		Assert.is(!JavaUtil.containsAny(id, OTConstants.illegals + " "), "Terminal app id cannot contain spaces or:" + OTConstants.illegals);
		Assert.is(!JavaUtil.containsAny(name, OTConstants.illegals), "Terminal app name cannot contain:" + OTConstants.illegals);
		Assert.is(!JavaUtil.containsAny(version, OTConstants.illegals), "Terminal app version cannot contain:" + OTConstants.illegals);
		if(JREUtil.isCompiled())
			Assert.is(this.appClass == null);//TODO: remove after debugging
		this.id = id;
		this.name = name;
		this.version = version;
		this.force = force;
		this.pause = pause;
		this.softPause = System.getProperty("ot.sp") != null;
		this.shouldLog = System.getProperty("ot.log") != null;
	}

	public String getTitle()
	{
		return this.name + " " + this.version;
	}
	
	/**
	 * Results should be consistent based on the OS Terminal or other data as it's called multiple times between objects
	 */
	public Profile getProfile()
	{
		return this.profile;
	}

	public void setProfile(Profile p)
	{
		this.profile = p;
	}
	
	/**
	 * returns the specified ConsoleHost aka the UI for the terminal
	 * @return null if you want it to use start commands instead of specifying the UI type
	 */
	public ConsoleHost getConsoleHost()
	{
		if(this.conHost != null)
		{
			switch(this.conHost)
			{
				case "wt":
					return new WTHost(this);
				case "":
					break;
				default:
					throw new IllegalArgumentException("no console host handler registered for:\"" + this.conHost + "\"");
			}
		}
		return null;
	}
	
	public TerminalExe getTerminalExe()
	{
		switch(this.terminal)
		{
			case "cmd":
				return new BatchExe(this);
			case "powershell":
				this.bootLogger.println("Powershell has some bugs especially in windows 10 and is not recommended for your application's default! The Start-Process command may have different bugs in different versions and not boot the CLI");
				return new PowerShellExe(this);
			case "Terminal.app":
				return new MacBashExe(this);
		}
		if(TerminalUtil.linux_terminals.contains(this.terminal))
		{
			if(this.linuxCmdsExe.contains(this.terminal))
				return new LinuxCmdTerminalExe(this);
			
			switch(this.terminal)
			{
				case "guake":
					return new GuakeTerminalExe(this);
				case "tilda":
					return new LinuxCmdTerminalExe(this);
				case "sakura":
					return new LinuxCmdTerminalExe(this);
				case "kgx":
					return new LinuxCmdTerminalExe(this);
				case "terminus":
					return new LinuxCmdTerminalExe(this);
				case "terminology":
					return new LinuxCmdTerminalExe(this);
				case "vala-terminal":
					return new ValaTerminalExe(this);
			}
			LinuxBashExe bash = new LinuxBashExe(this);
			if(this.terminal.equals("terminalpp"))
				bash.quoteCmd = true;
			return bash;
		}
		return null;
	}
	
	/**
	 * load the configurations for this terminal app
	 */
	public void load(boolean ipc)
	{
		this.loadConfig();
		this.initPalettes();
		this.loadSession();
		this.enableLoggers();
		if(ipc)
		{
			this.startPipeManager();
		}
		else
		{
			this.isShellDisabled = true;
			try 
			{
				if(System.console() != null)
				{
					this.loadColors();
					this.applyProperties();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * apply properties when new CLI doesn't open like title and CLI profiles
	 */
	public void applyProperties() 
	{
		try
		{
			System.out.print("]0;" + this.getTitle() + "");
			System.out.flush();
		}
		catch (Exception e){e.printStackTrace();}
	}

	public void loadConfig() 
	{
		MapConfig cfg = new MapConfig(new File(OTConstants.configs, this.id + ".cfg"));
		cfg.load();
		this.terminal = cfg.get("terminal", this.terminal).trim();
		this.conHost = cfg.get("conHost", this.conHost).trim();
		if(!TerminalUtil.isExeValid(this.terminal))
		{
			this.terminal = TerminalUtil.getTerminal();
			Assert.is(this.terminal != null, "Unable to find terminal:" + System.getProperty("os.name") + " report to https://github.com/jredfox/OpenTerminal/issues");
			cfg.set("terminal", this.terminal);
		}
		if(!this.conHost.isEmpty() && !TerminalUtil.isExeValid(this.conHost))
		{
			this.conHost = "";
			cfg.set("conHost", this.conHost);
		}
		this.ANSI4BIT = cfg.get("ANSI-4bit-Colors", this.ANSI4BIT);
		this.ANSI4BitDatFlag = !this.ANSI4BIT;
		
		String[] lcmds = cfg.get("linuxCmdExe", "").split(";");
		for(String c : lcmds)
		{
			c = c.trim();
			if(!c.isEmpty())
				this.linuxCmdsExe.add(c);
		}
		
		String[] lflags = cfg.get("linuxFlags", "").split(";");
		for(String f : lflags)
		{
			f = f.trim();
			if(f.isEmpty())
				continue;
			String[] arr = f.split("=");
			this.linuxFlags.put(arr[0].trim(), arr[1].trim());
		}
		
		this.ansi4bitPalette = cfg.get("ansi4bitPalette", "").trim();
		this.syncColorModeThread = cfg.get("syncColorModeThread", this.syncColorModeThread);
		cfg.save();
		
		//get the cachedColorTerm from the terminal.dat file if it exists
		List<String> tl = IOUtils.getFileLines(new File(OTConstants.home, "cache/" + this.terminal + ".dat"));
		this.cachedColorTerm = tl.isEmpty() ? "" : tl.get(0).trim();
		if(this.colors.getColorMode(this.cachedColorTerm) == TermColors.ANSI4BIT)
		{
			System.out.println("setting ansi4bit to true from loadConfig()");
			this.ANSI4BIT = true;
		}
	}
	
	public void initPalettes()
	{
		String p = "";
		String bp = "resources/jml/ot/colors/";
		if(!this.ansi4bitPalette.isEmpty() && TerminalApp.class.getClassLoader().getResource(bp + this.ansi4bitPalette) != null)
			p = this.ansi4bitPalette;//allow configured palette colors from the user
		else if(TerminalUtil.isWindowsTerm(this.terminal))
			p = "ansi4bit-windows-10.csv";
		else if(TerminalUtil.isMacTerm(this.terminal))
			p = "ansi4bit-terminal.app.csv";
		else
			p = "ansi4bit-gnome-tango.csv";//default to tango as it's linux's default
		this.colors.pickerAnsi4Bit = new Palette(bp + p);
		this.logBoot("ANSI 4-bit Color Palette:" + bp + p);
	}
	
	public void loadSession()
	{
		this.session = OTConstants.LAUNCHED ? new File(OTConstants.tmp, System.getProperty("ot.s")) : JREUtil.getMSDir(OTConstants.tmp);
		this.sessionName = this.session.getName();
		
		//cleanup previous sessions
		if(!OTConstants.LAUNCHED)
			IOUtils.deleteDirectory(this.session.getParentFile(), false);
	}

	public void enableLoggers()
	{
		if(this.shouldLog)
		{
			try
			{
				File log = this.getLogger();
				FileUtil.create(log);
				System.setOut(new WrappedPrintStream(System.out, log));
				System.setErr(new WrappedPrintStream(System.err, log));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public File getLogger() 
	{
		if(this.logger == null)
			this.logger = new File(OTConstants.home, "logs/" + this.id + "/log-" + this.sessionName + ".txt");
		return this.logger;
	}
	
	/**
	 * called during boot to setup the IPC PipeManager
	 */
	public void startPipeManager()
	{
		this.manager = new FilePipeManager(OTConstants.LAUNCHED, this.replaceSYSO, this.session, this.shouldLog ? this.getLogger() : null);
		this.manager.start();
		if(OTConstants.LAUNCHED)
			this.sendColors();
	}
	
	/**
	 * Non Shell Pause in java that fires at {@link System#exit(int)} or at the end of {@link OTMain#main(String[])} on CLI client side
	 */
	public void pause(boolean soft) 
	{
		if(soft && this.softPause || !soft && this.pause && this.isShellDisabled)
		{
			this.pause();
		}
	}
	
	/**
	 * direct pause no checks
	 */
	@SuppressWarnings("resource")
	public void pause() 
	{
		Profile p = this.getProfile();
		String msg = p != null ? (this.colors.colorMode == TermColors.TRUE_COLOR ? p.getPauseMsg() : p.getPauseLowResMsg()) : OTConstants.pauseMsg;
		System.out.print(msg);
		System.out.flush();//ensure it's written right away
		new Scanner(System.in).nextLine();
	}
	
	/**
	 * fired once the CLI closes.
	 */
	public void closeCLIEvent()
	{
		//TODO:make it work with the PID isAlive() update
		if(this.exitOnCLI)
		{
			System.out.println("Host process is closing due to CLI closing");
			System.exit(0);
		}
	}
	
	public static class Profile
	{
		public Color bg;
		public Color fg;
		public String ansiFormat;
		protected String pauseMsg = OTConstants.pauseMsg;//truecolor pause message
		protected String pauseLowResMsg = OTConstants.pauseMsg;//xterm-256 or ansi4bit colored string
		public boolean hasColoredErr;
		public Color bgErr;
		public Color fgErr;
		public String ansiFormatErr;
		public String wtTab;//WT Tab color
		public String wtScheme;//WT color scheme
		public boolean wtFullScreen;
		public boolean wtMaximized;
		/**
		 * the profile name will be equal to the id for custom profiles due to importing is always the file name of the import
		 */
		public String mac_profileName = "";
		/**
		 * make sure you set this unique to your application so it doesn't conflict with another custom terminal profile
		 * mac profile files must start with oti.(yourid).profile as the title name
		 */
		public String mac_profileId = "";
		/**
		 * the terminal profile path within your jar from the root directory
		 */
		public String mac_profilePath = "";
		
		public Profile()
		{
			
		}
		
		public Profile(Color b, Color t)
		{
			this.bg = b;
			this.fg = t;
		}
		
		/**
		 * pre-determined macOs terminal profile the End-USER will have this profile installed before executing your program
		 */
		public static Profile newMac(String profileName)
		{
			Profile p = new Profile();
			p.mac_profileName = profileName;
			return p;
		}
		
		/**
		 * create your new macOs terminal custom profile
		 */
		public static Profile newMac(String prid, String pp)
		{
			Profile p = new Profile();
			p.mac_profileId = prid;
			p.mac_profileName = prid;
			p.mac_profilePath = pp;
			return p;
		}
		
		public void setPauseMsg(String pause)
		{
			Assert.is(!pause.contains(AnsiColors.ESC), "Not for use of colored pause messages! Use Profile#setPause(String hdPause, String lowResPause) instead");
			this.pauseMsg = pause;
			this.pauseLowResMsg = pause;
		}
		
		public void setPauseMsg(String hdPause, String lowResPause)
		{
			this.pauseMsg = hdPause;
			this.pauseLowResMsg = lowResPause;
		}
		
		public String getPauseMsg()
		{
			return this.pauseMsg;
		}
		
		public String getPauseLowResMsg()
		{
			return this.pauseLowResMsg;
		}
	}
	
	/**
	 * execute the command in the terminal UI TerminalApp configurations override
	 */
	public String getLinuxExe()
	{
		String cflag = this.linuxFlags.get(this.terminal);
		return cflag != null ? cflag : TerminalUtil.getLinuxExe(this.terminal);
	}
	
	public TermColors getBootTermHD()
	{
		return this.ANSI4BIT ? TermColors.ANSI4BIT : TermColors.TRUE_COLOR;
	}
	
	public TermColors getBootTermLowRes()
	{
		return this.ANSI4BIT ? TermColors.ANSI4BIT : TermColors.XTERM_256;
	}

	/**
	 * get shell true color or if in ansi 4 bit or an empty string if the profile is null
	 */
	public String getBootTrueColor(Profile p)
	{
		if(p == null)
			return "";
		return (p.ansiFormat == null ? "" : p.ansiFormat) + this.colors.formatColor(this.getBootTermHD(), p.bg, p.fg, "", false);
	}
	
	/**
	 * get the lower resolution color that's non rgb. either ANSI 4bit or xterm-256 for the shell
	 */
	public String getBootPaletteColor(Profile p)
	{
		if(p == null)
			return "";
		return (p.ansiFormat == null ? "" : p.ansiFormat) + this.colors.formatColor(this.getBootTermLowRes(), p.bg, p.fg, "", false);
	}
	
	/**
	 * use this for the colored pause option as we cannot assume the color mode is always true color. 
	 * this is used mostly for the colored pause via the shell
	 */
	public String formatColorHD(Color bg, Color fg, String s)
	{
		return this.colors.formatColor(this.getBootTermHD(), bg, fg, s, true);
	}
	
	/**
	 * use this for the colored pause low res option as we cannot assume the color mode is always xterm-256 / ansi4bit. 
	 * this is used mostly for the colored pause via the shell
	 */
	public String formatColorLowRes(Color bg, Color fg, String s)
	{
		return this.colors.formatColor(this.getBootTermLowRes(), bg, fg, s, true);
	}
	
	public void setPauseMsg(Color bg, Color fg, String msg, Profile p)
	{
		p.setPauseMsg(this.formatColorHD(bg, fg, msg), this.formatColorLowRes(bg, fg, msg));
	}

	public PrintStream createBootLogger()
	{
		try
		{
			File flog = new File(OTConstants.home, "logs/" + id + "/boot" + (OTConstants.LAUNCHED ? "-client" : "") + ".txt");
			FileUtil.create(flog);
			this.bootLogger = new PrintStream(new FileOutputStream(flog), true);
			return this.bootLogger;
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void logBoot(String msg) 
	{
		this.logBoot(msg, true);
	}
	
	public void logBoot(String msg, boolean ln) 
	{
		if(this.canLogBoot)
		{
			if(ln)
				this.bootLogger.println(msg);
			else
				this.bootLogger.print(msg);
		}
	}

	/**
	 * retrieve session Color ENV from CLI client
	 * We don't need a constant Client to Server IPC Pipe so the client just sends a file
	 */
	public void loadColors() throws IOException 
	{
		boolean check = false;
		String mode = this.isShellDisabled ? this.getTermColors() : this.cachedColorTerm;
		if(this.ANSI4BIT)
		{
			this.colors.setColorMode(TermColors.ANSI4BIT);//ignore cached and current CLI's data in this mode
			check = this.ANSI4BitDatFlag;//only perform check if the ansi4bitcolor mode wasn't true from the user config
		}
		else
		{
			long ms = System.currentTimeMillis();
			if(mode.isEmpty())
			{
				mode = this.getTermColors();
				if(mode == null)
				{
					mode = TerminalUtil.isWindowsTerm(this.terminal) ? TermColors.TRUE_COLOR.toString() : TermColors.XTERM_256.toString();//safe to assume for most terminals xterm-256 as the fallout
					this.logBoot("CRITICAL Unable to Obtain Color mode Asumming ColorMode:" + mode);
					System.err.println("CRITICAL Unable to Obtain Color mode Asumming ColorMode:" + mode);
				}
				else
				{
					this.logBoot("Saving:" + this.terminal + ":" + this.colors.getColorMode(mode));
					this.updateColorModeCache(mode);
				}
			}
			else
			{
				check = true;//don't re-check colors from the color thread if it's already fetched
			}
			this.logBoot("Get CLI Color in:" + (System.currentTimeMillis()-ms) + " COLOR ENV:" + mode);
			this.colors.setColorMode(mode);//safe to assume true color as ansi4bit is never true here
		}
		//enable colors on server when IPC is disabled
		if(this.isShellDisabled && TerminalUtil.shouldEnableColors(this.terminal))
		{
			System.setProperty("ot.w", "true");
			AnsiColors.enableCmdColors();
		}
		Profile p = this.getProfile();
		if(p != null)
		{
			this.colors.setReset(p.bg, p.fg, p.ansiFormat, true);
			if(p.hasColoredErr)
				System.setErr(new ColoredPrintStream(p.bgErr, p.fgErr, p.ansiFormatErr, this.colors, System.err));
		}
		if(check && !this.isShellDisabled)
			this.startTermColorThread(mode);//TODO move to end
		this.logBoot("TermColors:" + this.colors.colorMode + " CachedColor:" + this.colors.colors);
	}

	/**
	 * check the CLI's $TERMCOLOR $TERM for an update desynced from the main thread
	 */
	public void startTermColorThread(String mode) throws IOException 
	{
		this.logBoot("Starting $TERMCOLOR $TERM check from CLI on desynced thread");
		Thread t = new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					TermColors oldMode = TerminalApp.this.colors.getColorMode(mode);
					TermColors newMode = TerminalApp.this.colors.getColorMode(TerminalApp.this.getTermColors());
					if(oldMode != newMode)
					{
						TerminalApp.this.updateColorModeCache(newMode.toString());
						//update the server & CLI to the new color mode
						if(TerminalApp.this.syncColorModeThread)
						{
							TerminalApp.this.colors.updateColorMode(newMode, false);
							System.out.print(AnsiColors.getSoftCls());//clear line feed without deleting them
							System.out.println("AnsiColors Updated: $TERMCOLOR:" + TerminalApp.this.terminal + " from:" + oldMode + " to:" + TerminalApp.this.colors.colorMode);
						}
					}
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public void updateColorModeCache(String mode) throws FileNotFoundException 
	{
		IOUtils.saveFileLines(Arrays.asList(this.colors.getColorMode(mode).toString()), new File(OTConstants.home, "cache/" + this.terminal + ".dat"), true);
	}

	public String getTermColors() throws IOException 
	{
		if(this.isShellDisabled)
			return this.getColorsEnv();
		File noREQ = new File(this.session, "ot-noREQ.txt");
		FileUtils.create(noREQ);
		PipeInputStream pipedIn = new PipeInputStream(noREQ, null, null, 1L);
		pipedIn.timeout = 15000L;//Set the timeout to 15s
		BufferedReader reader = IOUtils.getReader(pipedIn);
		String mode = reader.readLine();
		IOUtils.close(reader);
		return mode;
	}

	/**
	 * on CLI Client Side sending to Host Server Side ENV variables such as colors
	 */
	public void sendColors()
	{
		String col = this.getColorsEnv();
		IOUtils.saveFileLines(Arrays.asList(col), new File(this.session, "ot-noREQ.txt") , true);
	}

	public String getColorsEnv() 
	{
		return System.getenv("TERM") + System.getenv("COLORTERM");
	}

}
