package jml.ot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException
	{
		long ms = System.currentTimeMillis();
		TerminalApp app = new TerminalApp("test", "Test App", "1.0.0", false) 
		{
			@Override
			public Profile getProfile() 
			{
				if(OSUtil.isWindows())
				{
					Profile p = new Profile("0", "a");
					if(this.terminal.equals("powershell"))
					{
						p.wtScheme = "Campbell Powershell";
						p.wtTab = "42f5ec";
						p.wtMaximized = true;
//						p.wtFullScreen = true;
					}
					return p;
				}
				else if(OSUtil.isMac())
				{
//					Profile p = Profile.newMac("jredfox.openterminal.blackglass", "resources/jml/ot/mac/BlGlass.terminal");
//					Profile p = Profile.newMac("jredfox.openterminal.purpleCollege", "resources/jml/ot/mac/college.terminal");
					Profile p = Profile.newMac("Red Sands");
					return p;
				}
				return null;
			}
		};
//		app.pause = false;
		OpenTerminal.open(app);
		System.out.println("launch in:" + (System.currentTimeMillis() - ms) + "ms");
	}
	
	public static String findOnPath(String name)
	{
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator))
	    {
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute())
	        {
	            return file.getAbsolutePath();
	        }
	    }
	    throw new AssertionError("should have found the executable");
	}
}
