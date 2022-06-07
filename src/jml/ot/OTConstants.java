package jml.ot;

import java.io.File;

import jredfox.common.utils.JREUtil;

public class OTConstants {
	
	public static final String OTVERSION = "1.0.0";
	public static final File home = new File(OSUtil.getAppData().getAbsoluteFile(), "OpenTerminal");
	public static final File configs = new File(home, "configs");
	public static final File profiles = new File(home, "profiles");
	public static final File scripts = new File(home, "scripts");
	public static final File start = new File(scripts, "start");
	public static final File boot = new File(scripts, "boot");
	public static final File nullFile = new File("");
	public static final File userDir = JREUtil.getProgramDir().getAbsoluteFile();
	public static final String java_home = "\"" + System.getProperty("java.home") + "/bin/java\"";
	public static final String args = "-Dot.l -cp \"" + System.getProperty("java.class.path") + "\" jml.ot.OTMain";

}
