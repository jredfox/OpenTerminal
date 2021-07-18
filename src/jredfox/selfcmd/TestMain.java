package jredfox.selfcmd;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import jredfox.common.os.OSUtil;

public class TestMain {
	
	public static void main(String[] args) throws IOException, RuntimeException, URISyntaxException
	{
		if(System.console() == null)
			SelfCommandPrompt.setUserDir(new File(OSUtil.getAppData().getPath() + "/Music"));
		args = SelfCommandPrompt.runWithCMD("test_app", "Test App", args);
//		String cd = OSUtil.getAppData().getPath();
//		System.setProperty("user.dir", cd);
		System.out.println(new File("").getAbsolutePath());
		System.out.println(new File(".").getCanonicalPath());
		System.out.println(Paths.get("").toAbsolutePath());
		System.out.println("user.dir:" + System.getProperty("user.dir"));
//		System.out.println("debug:" + System.getProperty("java.class.path"));
	}

}
