package jredfox.terminal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * going to be replaced with a virtual wrapper to get it working with eclipse's jarInJar loader without extraction
 */
@Deprecated
public class OpenTerminalWrapper {
	
	public static void main(String[] args)
	{
		try
		{
			String className = args[0];
			System.setProperty("openterminal.app.mainclass", className);//because eclipse's jar in jar loader sets a class loader it wipes static fields set a system property instead
			Class<?> mainClass = Class.forName(className);
			String[] programArgs = new String[args.length - 1];
			System.arraycopy(args, 1, programArgs, 0, programArgs.length);
			Method method = mainClass.getMethod("main", String[].class);
			method.invoke(null, new Object[]{programArgs});
		}
		catch(InvocationTargetException e)
		{
			if(e.getCause() != null)
				e.getCause().printStackTrace();
			else
				e.printStackTrace();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		if(OpenTerminal.INSTANCE.app.shouldPause())
		{
			OpenTerminal.INSTANCE.app.pause();
		}
	}

}
