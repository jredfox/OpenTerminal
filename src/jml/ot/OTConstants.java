package jml.ot;

import java.io.File;

public class OTConstants {
	
	public static final File home = new File(System.getProperty("user.home"), "OpenTerminal").getAbsoluteFile();
	public static final File scripts = new File(home, "scripts");
	public static final File start = new File(scripts, "start");
	public static final File boot = new File(scripts, "boot");
	public static final File userDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
	public static final String java_home = "\"" + System.getProperty("java.home") + "/bin/java\"";
	public static final String args = "-Dot.l=t -cp \"" + System.getProperty("java.class.path") + "\" jml.ot.OTMain";
}
