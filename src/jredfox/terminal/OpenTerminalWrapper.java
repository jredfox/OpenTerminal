package jredfox.terminal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jredfox.terminal.app.TerminalApp;

/**
 * virtual wrapper because hard wrappers causes issues with other wrappers including eclipe's jarInJar loader
 */
public class OpenTerminalWrapper {
	
	public static void run(TerminalApp app, String[] args)
	{
		boolean err = false;
		try
		{
//			Runtime.getRuntime().addShutdownHook(
//				new Thread()
//				{
//					@Override
//					public void run()
//					{
//						if(app.shouldPause())
//							app.pause();	
//					}
//				}	
//			);
			Method method = app.mainClass.getMethod("main", String[].class);
			if(args.length == 0)
			{
//				args = app.getWrappedArgs();//TODO: also have a msg without line feed enforced
			}
			System.gc();
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
		
		if(app.shouldPause())
		{
			app.pause();//TODO: add on shutdown hook as well
		}
		System.exit(err ? -1 : 0);
	}

}
