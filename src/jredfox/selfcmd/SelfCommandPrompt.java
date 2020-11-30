package jredfox.selfcmd;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jredfox.filededuper.util.IOUtils;
import jredfox.selfcmd.jconsole.JConsole;
import jredfox.selfcmd.util.OSUtil;
/**
 * @author jredfox. Credits to Chocohead#7137 for helping me find the windows start command
 * this class is a wrapper for your program. It fires command prompt and stops it from quitting without user input
 */
public class SelfCommandPrompt {
	
	public static final String VERSION = "1.5.0";
	
	/**
	 * args are [shouldPause, mainClass, programArgs]
	 */
	public static void main(String[] args)
	{
		boolean shouldPause = Boolean.parseBoolean(args[0]);
		
		try
		{
			Class<?> mainClass = Class.forName(args[1]);
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
	 * NOTE: is WIP and doesn't take input currently use shell / batch files for unsupported oses in the mean time to run the jar
	 * supports all platforms no need to reboot, supports debugging and all ides, and supports shutdown hooks
	 */
	public static JConsole startJConsole(String appName)
	{	
		JConsole console = new JConsole(appName)
		{
			@Override
			public boolean isJavaCommand(String[] command){return true;}//always return true we do not support os commands in JConsole

			@Override
			public boolean shutdown(){return true;}
		};
		console.setEnabled(true);
		return console;
	}
	
	/**
	 * reboot your application with a command prompt terminal. Note if you hard code your mainClass instead of using the above method it won't support all compilers like eclipse's jar in jar loader
	 * NOTE: doesn't support debug function as it breaks ides connection proxies to the jvm agent's debug.
	 * before calling this if you have jvmArguments for like ports or connections close them before rebooting
	 */
	public static void runwithCMD(String[] args, String appName, String appId, boolean onlyCompiled, boolean pause)
	{
		runwithCMD(getMainClass(), args, appName, appId, onlyCompiled, pause);
	}
	
	/**
	 * reboot your application with a command prompt terminal. Note if you hard code your mainClass instead of using the above method it won't support all compilers like eclipse's jar in jar loader
	 * NOTE: doesn't support debug function as it breaks ides connection proxies to the jvm agent's debug.
	 * before calling this if you have jvmArguments for like ports or connections close them before rebooting
	 */
	public static void runwithCMD(Class<?> mainClass, String[] args, String appName, String appId, boolean onlyCompiled, boolean pause) 
	{
		boolean compiled = isCompiled(mainClass);
		if(!compiled && onlyCompiled || compiled && System.console() != null || isDebugMode() || SelfCommandPrompt.class.getName().equals(getMainClassName()))
		{
			return;
		}
		rebootWithTerminal(mainClass, args, appName, appId, pause);
	}
	
	/**
	 * this method is a directly calls commands to reboot your app with a command prompt terminal. 
	 * do not call this directly without if statements it will recursively reboot infinitely
	 */
	public static void rebootWithTerminal(Class<?> mainClass, String[] args, String appName, String appId, boolean pause)
	{
        try
        {
        	String str = getProgramArgs(args, " ");
            String argsStr = " " + mainClass.getName() + (str.isEmpty() ? "" : " " + str);
            String jvmArgs = getJVMArgs();
            String os = System.getProperty("os.name").toLowerCase();
            String command = "java " + (jvmArgs.isEmpty() ? "" : jvmArgs + " ") + "-cp " + System.getProperty("java.class.path") + " " + SelfCommandPrompt.class.getName() + " " + pause + argsStr;
            if(os.contains("windows"))
            {
            	Runtime.getRuntime().exec("cmd /c start " + "\"" + appName + "\" " + command);//power shell isn't supported as it screws up with the java -cp command when using the gui manually
            }
            else if(os.contains("mac") || os.contains("linux"))
            {
            	File javacmds = new File(OSUtil.getAppData(), "SelfCommandPrompt/shellsripts/" + appId + ".sh");
            	List<String> cmds = new ArrayList<>();
            	cmds.add("#!/bin/bash");
            	cmds.add("set +v");
            	cmds.add("echo -n -e \"\\033]0;" + appName + "\\007\"");
            	cmds.add("cd " + getProgramDir().getAbsolutePath());//enforce same directory with mac's redirects you never know where you are
            	cmds.add(command);
            	IOUtils.saveFileLines(cmds, javacmds, true);
            	IOUtils.makeExe(javacmds);
            	if(os.contains("mac"))
            		Runtime.getRuntime().exec("/bin/bash -c " + "osascript -e \"tell application \\\"Terminal\\\" to do script \\\"" + javacmds.getAbsolutePath() + "\\\"\"");
            	else
            		Runtime.getRuntime().exec(OSUtil.getTerminal() + " -x " + javacmds.getAbsolutePath());
            }
            else
            {
            	SelfCommandPrompt.startJConsole(appName);//for unsupported os's use the java console
            	return;//do not exit the application so return from the method
            }
            Runtime.getRuntime().gc();
            System.exit(0);
        }
        catch (Exception e)
        {	
			SelfCommandPrompt.startJConsole(appName);//use JConsole as a backup in case they are on a very old os version
        	e.printStackTrace();
			System.out.println("JCONSOLE STARTING:");
		}
	}

	/**
	 * checks if the jar is compiled based on the main class
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean isCompiled()
	{
		return isCompiled(getMainClass());
	}
	
	/**
	 * checks per class if the jar is compiled
	 * @throws UnsupportedEncodingException 
	 */
	public static boolean isCompiled(Class<?> mainClass)
	{
		try 
		{
			File file = getFileFromClass(mainClass);
			return getExtension(file).equals("jar") || getMainClassName().endsWith("jarinjarloader.JarRsrcLoader");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * get a file from a class
	 */
	public static File getFileFromClass(Class<?> clazz) throws UnsupportedEncodingException
	{
		String jarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();//get the path of the currently running jar
		String fileName = URLDecoder.decode(jarPath, "UTF-8").substring(1);
		return new File(fileName);
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
	
	public static String getMainClassName()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		String actualMain = main.getClassName();
		return actualMain;
	}
	
	public static Class<?> getMainClass()
	{
		Class<?> mainClass = null;
		try 
		{
			String className = getMainClassName();
			mainClass = Class.forName(className);
		} 
		catch (ClassNotFoundException e1) 
		{
			e1.printStackTrace();
		}
		return mainClass;
	}
	
	public static String getProgramArgs(String[] args, String sep) 
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
	 * incompatible with eclipse's jar in jar loader. Use this to enforce your program's directory is synced with your jar after calling runWithCMD
	 */
	public static void syncUserDirWithJar()
	{
		try 
		{
			setUserDir(getFileFromClass(getMainClass()).getParentFile());
		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void setUserDir(File file)
	{
		System.setProperty("user.dir", file.getAbsolutePath());
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