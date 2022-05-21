package jml.ot;

import java.io.File;

public class OTConstants {
	
	public static final File home = new File(System.getProperty("user.home"), "OpenTerminal").getAbsoluteFile();
	public static final File home_scripts = new File(home, "scripts");
	public static final File userDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
	public static final String java_home = "\"" + System.getProperty("java.home") + "/bin/java\"";
	public static final String args = "-cp \"" + System.getProperty("java.class.path") + "\" jml.ot.OTMain";
}
