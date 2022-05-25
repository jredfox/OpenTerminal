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
				if(OSUtil.isWindows())
					return new Profile("3", "f");
				return null;
			}
		};
		OpenTerminal.open(app);
		System.out.println("launch in:" + (System.currentTimeMillis() - ms) + "ms");
	}

}
