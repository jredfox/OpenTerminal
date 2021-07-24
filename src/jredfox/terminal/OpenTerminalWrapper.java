package jredfox.terminal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jredfox.common.exe.ExeBuilder;
import jredfox.common.utils.JREUtil;
import jredfox.common.utils.JavaUtil;
import jredfox.terminal.app.TerminalApp;
import jredfox.terminal.app.TerminalAppWrapped;

public class OpenTerminalWrapper {
	
	/**
	 * virtual wrapper because hard wrappers forces OpenTerminal to be extracted when using eclipe's JarInJar loader
	 */
	public static void run(TerminalApp app, String[] args)
	{
		boolean err = false;
		try
		{
			Method method = app.mainClass.getMethod("main", String[].class);
			if(app instanceof TerminalAppWrapped)
				args = ((TerminalAppWrapped)app).getWrappedArgs(args);
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
			app.pause();
		}
		System.exit(err ? -1 : 0);
	}
	
	/**
	 * this is the hard pause option. It wraps the TermainApp#mainClass#main(String[] args) and catches {@link System#exit(int)} slightly slower then virtualWrapper as a new Process has to be created
	   NOTE: requires OpenTerminal library to be extracted into a jar for JarInJar loader when this option is used so java can find the main class
	 */
	public static void main(String[] args) throws IOException
	{
		TerminalApp app = new TerminalApp(args);//TODO: fix hard coded default TerminalApp option here
		app.fromProperties();
		if(app instanceof TerminalAppWrapped)
			args = ((TerminalAppWrapped)app).getWrappedArgs(args);
		
		ExeBuilder b = new ExeBuilder();
		b.addCommand("java");
		List<String> jvm = JavaUtil.asArray(JREUtil.getJVMArgs());
		b.addCommand(OpenTerminalUtil.writeProperty(jvm, OpenTerminalConstants.launchStage, OpenTerminalConstants.exe));
		b.addCommand(OpenTerminalUtil.wrapProgramArgs(jvm));
		b.addCommand("-cp");
		b.addCommand("\"" + System.getProperty("java.class.path") + "\"");
		b.addCommand(app.mainClass.getName());
		b.addCommand(OpenTerminalUtil.wrapProgramArgs(app.programArgs));
		Process p = OpenTerminalUtil.runInTerminal(app.terminal, b.toString());
		JREUtil.sleep(100);
		while(p.isAlive())
		{
			JREUtil.sleep(500);//don't ping Process#isAlive constantly as it slows down the process
		}
		if(app.shouldPause())
		{
			app.pause();
		}
	}

}
