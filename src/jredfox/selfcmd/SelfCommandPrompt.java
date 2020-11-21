package jredfox.selfcmd;

import java.io.Console;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Scanner;
/**
 * @author jredfox. Credits to Chocohead#7137 for helping
 * this class is a wrapper for your program. It fires command prompt and stops it from quitting without user input
 */
public class SelfCommandPrompt {
	
	public static void main(String[] args)
	{
		try
		{
			Class mainClass = Class.forName(args[0]);
			String[] actualArgs = new String[args.length - 1];
			System.arraycopy(args, 1, actualArgs, 0, actualArgs.length);
			Method method = mainClass.getMethod("main", String[].class);
			method.invoke(null, new Object[]{actualArgs});
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		
		//got to make sure to pause the command prompt until the user has read the info
		Scanner old = new Scanner(System.in);
		Scanner scanner = old.useDelimiter("\n");
		System.out.println("Press ENTER to continue:");
		scanner.next();
		old.close();
		scanner.close();
	}
	
	/**
	 * supports all platforms
	 */
	public static void runWithJavaCMD(String appTitle, boolean onlyCompiled)
	{
		//TODO:
	}
	
	/**
	 * use this command to support wrappers like eclipses jar in jar loader
	 */
	public static void runwithCMD(String[] args, String appTitle, boolean onlyCompiled)
	{
		runwithCMD(getMainClass(), args, appTitle, onlyCompiled);
	}
	
	/**
	 * run your current program with command prompt and close your current program without one. Doesn't support wrappers unless you use {@link SelfCommandPrompt#getMainClass()}
	 */
	public static void runwithCMD(Class<?> mainClass, String[] args, String appTitle, boolean onlyCompiled) 
	{
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
            	
            	String os = System.getProperty("os.name").toLowerCase();
            	String command = "java " + "-cp " + System.getProperty("java.class.path") + " " + SelfCommandPrompt.class.getName() + argsStr;
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
			b.append(index + 1 != args.length ? s + sep : s);
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
