package jml.ot;

import java.io.File;
import java.io.IOException;

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
				return null;
			}
		};
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
