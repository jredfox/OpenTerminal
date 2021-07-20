package jredfox.terminal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * virtual wrapper because hard wrappers causes issues with other wrappers including eclipe's jarInJar loader
 */
public class OpenTerminalWrapper {
	
	public static void run(OpenTerminal ot, String[] args)
	{
		boolean err = false;
		try
		{
			Method method = ot.app.mainClass.getMethod("main", String[].class);
			method.invoke(null, new Object[]{args});
		}
		catch(InvocationTargetException e)
		{
			err = true;
			if(e.getCause() != null)
				e.getCause().printStackTrace();
			else
				e.printStackTrace();
		}
		catch(Throwable t)
		{
			err = true;
			t.printStackTrace();
		}
		
		if(ot.app.shouldPause())
		{
			ot.app.pause();
		}
		System.exit(err ? -1 : 0);
	}

}
