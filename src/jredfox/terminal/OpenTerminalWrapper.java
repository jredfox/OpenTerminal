package jredfox.terminal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;
import jredfox.terminal.app.TerminalAppWrapper;

public class OpenTerminalWrapper {
	
	/**
	 * virtual wrapper because hard wrappers forces OpenTerminal to be extracted when using eclipe's JarInJar loader
	 */
	public static void run(TerminalApp app)
	{
		System.setProperty(OpenTerminalConstants.launchStage, OpenTerminalConstants.exe);//set the state from wrapping to execute
		boolean err = false;
		try
		{
			String[] args = app instanceof TerminalAppWrapper ? (((TerminalAppWrapper)app).getWrappedArgs(app.getProgramArgs())) : app.getProgramArgs();
			Method method = app.mainClass.getMethod("main", String[].class);
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
		
		int exit = err ? -1 : 0;
		if(app.shouldPause(exit))
			app.pause();
		System.exit(exit);
	}
	
	/**
	 * this is the hard pause option. It wraps the TermainApp#mainClass#main(String[] args) and catches {@link System#exit(int)} slightly slower then virtualWrapper as a new Process has to be created
	   NOTE: requires OpenTerminal library to be extracted into a jar for JarInJar loader when this option is used so java can find the main class
	 */
	public static void main(String[] args) throws IOException
	{
		TerminalApp app = TerminalApp.newInstance(args);
		app.fromProperties();
		args = app instanceof TerminalAppWrapper ? (((TerminalAppWrapper)app).getWrappedArgs(args)) : app.getProgramArgs();
		
		ExeBuilder b = new ExeBuilder();
		b.addCommand("java");
		List<String> jvm = JavaUtil.asArray(JREUtil.getJVMArgs());
		b.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.launchStage, OpenTerminalConstants.exe));
		b.addCommand(OpenTerminalUtil.wrapProgramArgs(jvm));
		b.addCommand("-cp");
		b.addCommand("\"" + System.getProperty("java.class.path") + "\"");
		b.addCommand(app.mainClass.getName());
		b.addCommand(OpenTerminalUtil.wrapProgramArgs(args));
		Process p = OpenTerminalUtil.runInTerminal(app.terminal, b.toString(), app.userDir);
		JREUtil.sleep(100);
		while(p.isAlive())
		{
			
		}
		
		if(app.shouldPause(p.exitValue()))
			app.pause();
	}

}
