package jredfox.terminal;

import java.io.File;
import java.util.Scanner;

import jredfox.common.os.OSUtil;

public class OpenTerminalConstants {
	
	public static final String VERSION = "0.0.0-alpha";
	public static final String INVALID = "\"'`,";
	public static final Scanner scanner = new Scanner(System.in);
	public static final File data = new File(OSUtil.getAppData(), "OpenTerminal");
	public static final File scripts = new File(data, "scripts");
	public static final File closeMe = new File(scripts, "closeMe.scpt");
	public static final File start = new File(scripts, "start.scpt");
	public static final String launchStage = "openterminal.stage";
	public static final String init = "init";
	public static final String wrapping = "wrapping";
	public static final String exe = "exe";
	public static final String jvmArgs = "openterminal.jvmArgs";
	public static final String programArgs = "openterminal.programArgs";
	public static final int rebootExit = 20214097;
	public static final String splitter = "\u00a9\ud83e\udd82\u00a9";
	public static final String linefeed = splitter + "l";
	public static final String spacefeed = splitter + "s";
	
}
