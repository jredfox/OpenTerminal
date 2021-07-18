package jredfox.selfcmd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


public class TestMain {
	
	public static void main(String[] args) throws IOException, RuntimeException, URISyntaxException
	{
		SelfCommandPrompt.syncUserDirWithJar();
		args = SelfCommandPrompt.runWithCMD("test_app", "Test App", args);
//		System.out.println(ClassLoader.getSystemClassLoader().getResource("jredfox/selfcmd/TestMain.class"));
//		System.out.println(TestMain.class.getProtectionDomain().getCodeSource().getLocation());
//		System.out.println(System.getProperty("java.version"));
		System.out.println(new File("").getAbsolutePath());
//		System.out.println(new File(".").getCanonicalPath());
//		System.out.println(Paths.get("").toAbsolutePath());
//		System.out.println("user.dir:" + System.getProperty("user.dir"));
//		System.out.println("debug:" + System.getProperty("java.class.path"));
	}

}
