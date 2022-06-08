package jml.ot;

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
				if(TerminalUtil.isWindows())
				{
					Profile p = new Profile("3", "f");
					if(this.terminal.equals("powershell"))
					{
						p.wtScheme = "Campbell Powershell";
						p.wtTab = "42f5ec";
//						p.wtMaximized = true;
//						p.wtFullScreen = true;
					}
//					return p;
					return null;
				}
				else if(TerminalUtil.isMac())
				{
//					Profile p = Profile.newMac("jredfox.openterminal.blackglass", "resources/jml/ot/mac/BlGlass.terminal");
					Profile p = Profile.newMac("jredfox.openterminal.purpleCollege", "resources/jml/ot/mac/college.terminal");
//					Profile p = Profile.newMac("Red Sands");
					return p;
				}
				return null;
			}
		};
		if(TerminalUtil.isLinux())
		{
			app.terminal = "gnome-terminal";//set's the initial terminal the configuration overrides this from TerminalApp#getTerminalExe called by OpenTerminal#open
		}
//		app.pause = false;
		OpenTerminal.open(app);
		System.out.println("launch in:" + (System.currentTimeMillis() - ms) + "ms");
	}
}
