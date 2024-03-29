package jml.ot;

import java.io.File;

import jredfox.common.utils.JREUtil;

public class OTConstants {
	
	public static final String OTVERSION = "alpha-1.1.0";
	public static final String illegals = "\"';:$@";
	public static final String pauseMsg = "Press ENTER to continue...";
	public static final File home = new File(TerminalUtil.getAppData().getAbsoluteFile(), "OpenTerminal");
	public static final File configs = new File(home, "configs");
	public static final File profiles = new File(home, "profiles");
	public static final File scripts = new File(home, "scripts");
	public static final File tmp = new File(home, "tmp");
	public static final File start = new File(scripts, "start");
	public static final File boot = new File(scripts, "boot");
	public static final File userDir = JREUtil.getProgramDir().getAbsoluteFile();
	public static final boolean LAUNCHED = System.getProperty("ot.l") != null;
	public static final String java_home = "\"" + System.getProperty("java.home") + "/bin/java\"";
	public static final String args = "-Dot.l -cp \"" + System.getProperty("java.class.path") + "\" jml.ot.OTMain";

}
