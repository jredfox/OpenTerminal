package jredfox.selfcmd;

import java.io.Console;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.List;
import java.util.Scanner;
/**
 * @author jredfox. Credits to Chocohead#7137 for helping
 * this class is a wrapper for your program. It fires command prompt and stops it from quitting without user input
 */
public class SelfCommandPrompt {
	
	/**
	 * args are [shouldPause, mainClass, programArgs]
	 */
	public static void main(String[] args)
	{
		boolean shouldPause = Boolean.parseBoolean(args[0]);
		
		try
		{
			Class mainClass = Class.forName(args[1]);
			String[] programArgs = new String[args.length - 2];
			System.arraycopy(args, 2, programArgs, 0, programArgs.length);
			Method method = mainClass.getMethod("main", String[].class);
			method.invoke(null, new Object[]{programArgs});
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		if(shouldPause)
		{
			Scanner old = new Scanner(System.in);
			Scanner scanner = old.useDelimiter("\n");
			System.out.println("Press ENTER to continue:");
			scanner.next();
			old.close();
			scanner.close();
		}
	}

	/**
	 * supports all platforms no need to reboot, supports debugging and all ides
	 */
	public static void runWithJavaCMD(String appTitle, boolean onlyCompiled)
	{
		//TODO:
	}
	
	/**
	 * reboot your application with a command prompt terminal. Note if you hard code your mainClass instead of using the above method it won't support all compilers like eclipse's jar in jar loader
	 * NOTE: doesn't support debug function as it breaks ides connection proxies to the jvm agent's debug.
	 * before calling this if you have jvmArguments for like ports or connections close them before rebooting
	 */
	public static void runwithCMD(String[] args, String appTitle, boolean onlyCompiled, boolean pause)
	{
		runwithCMD(getMainClass(), args, appTitle, onlyCompiled, pause);
	}
	
	/**
	 * reboot your application with a command prompt terminal. Note if you hard code your mainClass instead of using the above method it won't support all compilers like eclipse's jar in jar loader
	 * NOTE: doesn't support debug function as it breaks ides connection proxies to the jvm agent's debug.
	 * before calling this if you have jvmArguments for like ports or connections close them before rebooting
	 */
	public static void runwithCMD(Class<?> mainClass, String[] args, String appTitle, boolean onlyCompiled, boolean pause) 
	{
		if(isDebugMode())
			return;
        Console console = System.console();
        if(console == null)
        {
            try
            {	
            	String str = toString(args, " ");
            	String argsStr = " " + mainClass.getName() + (str.isEmpty() ? "" : " " + str);
            	String jarPath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();//get the path of the currently running jar
            	String filename = URLDecoder.decode(jarPath, "UTF-8").substring(1);
            	boolean compiled = getExtension(new File(filename)).equals("jar");
            	if(!compiled && onlyCompiled)
            		return;
            	
            	String jvmArgs = getJVMArgs();
            	String os = System.getProperty("os.name").toLowerCase();
            	String command = "java " + (jvmArgs.isEmpty() ? "" : jvmArgs + " ") + "-cp " + System.getProperty("java.class.path") + " " + SelfCommandPrompt.class.getName() + " " + pause + argsStr;
            	if(os.contains("windows"))
            	{
            		new ProcessBuilder("cmd", "/c", "start", "\"" + appTitle + "\"", "cmd", "/c", command).start();
            	}
            	else if(os.contains("mac"))
            	{
            		new ProcessBuilder("/bin/bash", "-c", command).start();
            	}
            	else if(os.contains("linux"))
            	{
            		new ProcessBuilder("xfce4-terminal", "--title=" + appTitle, "--hold", "-x", command).start();
            	}
			}
            catch (Exception e)
            {
				e.printStackTrace();
			}
            System.exit(0);
        }
	}

	public static String getJVMArgs()
	{
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		StringBuilder b = new StringBuilder();
		String sep = " ";
		int index = 0;
		for(String s : arguments)
		{
			s = index + 1 != arguments.size() ? s + sep : s;
			b.append(s);
			index++;
		}
		return b.toString();
	}
	
	public static boolean isDebugMode()
	{
		return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
	}
	
	public static Class<?> getMainClass()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		String actualMain = main.getClassName();
		Class<?> mainClass = null;
		try 
		{
			mainClass = Class.forName(actualMain);
		} 
		catch (ClassNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		return mainClass;
	}
	
	public static String toString(String[] args, String sep) 
	{
		if(args == null)
			return null;
		StringBuilder b = new StringBuilder();
		int index = 0;
		for(String s : args)
		{
			String q = s.contains(" ") ? "\"" : "";
			s = index + 1 != args.length ? (q + s + q + sep) : (q + s + q);
			b.append(s);
			index++;
		}
		return b.toString();
	}
	
	/**
	 * get a file extension. Note directories do not have file extensions
	 */
	public static String getExtension(File file) 
	{
		String name = file.getName();
		int index = name.lastIndexOf('.');
		return index != -1 && !file.isDirectory() ? name.substring(index + 1) : "";
	}
	
	public static File getProgramDir()
	{
		return new File(System.getProperty("user.dir"));
	}
}
