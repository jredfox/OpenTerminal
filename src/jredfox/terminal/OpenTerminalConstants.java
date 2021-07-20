package jredfox.terminal;

import java.io.File;
import java.util.Scanner;

import jredfox.common.os.OSUtil;

public class OpenTerminalConstants {
	
	public static final String VERSION = "alpha.1.0.0";
	public static final String INVALID = "\"'`,";
	public static final Scanner scanner = new Scanner(System.in);
	public static final File data = new File(OSUtil.getAppData(), "OpenTerminal");
	public static final File scripts = new File(data, "scripts");
	public static final File closeMe = new File(scripts, "closeMe.scpt");
	public static final File start = new File(scripts, "start.scpt");
	public static final String wrapped = "openterminal.wrapped";
	public static final String launched = "openterminal.launched";
	
}