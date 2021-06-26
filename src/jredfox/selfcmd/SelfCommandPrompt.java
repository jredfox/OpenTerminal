package jredfox.selfcmd;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import jredfox.filededuper.Main;
import jredfox.filededuper.config.simple.MapConfig;
import jredfox.filededuper.util.DeDuperUtil;
import jredfox.filededuper.util.IOUtils;
import jredfox.selfcmd.exe.ExeBuilder;
import jredfox.selfcmd.jconsole.JConsole;
import jredfox.selfcmd.util.OSUtil;
/**
 * @author jredfox. Credits to Chocohead#7137 for helping me find the windows start command & System.getProperty("java.class.path);
 * this class is a wrapper for your program. It fires command prompt and stops it from quitting without user input
 */
public class SelfCommandPrompt {
	
	public static final String VERSION = "2.1.2";
	public static final String INVALID = OSUtil.getQuote() + "'`,";
	public static final File selfcmd = new File(OSUtil.getAppData(), "SelfCommandPrompt");
	public static final Scanner scanner = new Scanner(System.in);
	public static JConsole jconsole;
	public static String wrappedAppId;
	public static String wrappedAppName;
	public static Class<?> wrappedAppClass;
	public static String[] wrappedAppArgs;
	public static boolean wrappedPause;
	public static String background;
	public static boolean sameWindow;
	
	static
	{
		syncConfig();
	}
	
	/**
	 * args are [shouldPause, mainClass, programArgs...]
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args)
	{
		boolean shouldPause = Boolean.parseBoolean(args[0]);
		
		try
		{
			String className = args[1];
			System.setProperty("selfcmd.mainclass", className);//because eclipse's jar in jar loader sets a class loader it wipes static fields set a system property instead
			Class<?> mainClass = Class.forName(className);
			String[] programArgs = new String[args.length - 2];
			System.arraycopy(args, 2, programArgs, 0, programArgs.length);
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
		
		if(shouldPause)
		{
			Scanner scanner = new Scanner(System.in).useDelimiter("\n");//Warning says scanner is never closed but, useDelimiter returns itself
			System.out.println("Press ENTER to continue:");
			scanner.next();
		}
	}

	/**
	 * NOTE: this is WIP and doesn't support System.in redirect yet, and there are many other issues with it
	 */
	public static JConsole startJConsole(String appId, String appName)
	{	
		if(jconsole != null)
			return jconsole;
		JConsole console = new JConsole(appName)
		{
			@Override
			public boolean isJavaCommand(String[] command){return true;}//always return true we do not support os commands in JConsole

			@Override
			public boolean shutdown(){return true;}
		};
		console.setEnabled(true);
		jconsole = console;
		System.out.println("JCONSOLE isn't working yet. Please check back in a future version ;)");
		return jconsole;
	}
	
	/**
	 * ensure your program boots up with a command prompt terminal either a native configurable os terminal or JConsole.
	 * If you hard code your main class it won't support wrappers like eclipe's jar in jar loader.
	 * if you have connections in jvm args close them before reboot if {@link SelfCommandPrompt#hasJConsole()} returns false
	 * @Since 2.0.3
	 */
	public static String[] runWithCMD(String[] args)
	{
		String appId = suggestAppId();
		return runWithCMD(appId, appId, args);
	}
	
	/**
	 * ensure your program boots up with a command prompt terminal either a native configurable os terminal or JConsole.
	 * If you hard code your main class it won't support wrappers like eclipe's jar in jar loader.
	 * if you have connections in jvm args close them before reboot if {@link SelfCommandPrompt#hasJConsole()} returns false
	 * @Since 2.0.0
	 */
	public static String[] runWithCMD(String appId, String appName, String[] args)
	{
		return runWithCMD(appId, appName, args, false, true);
	}
	
	/**
	 * ensure your program boots up with a command prompt terminal either a native configurable os terminal or JConsole.
	 * If you hard code your main class it won't support wrappers like eclipe's jar in jar loader.
	 * if you have connections in jvm args close them before reboot !SelfCommandPrompt#hasJConsole()
	 * @Since 2.0.0
	 */
	public static String[] runWithCMD(String appId, String appName, String[] args, boolean onlyCompiled, boolean pause)
	{
		return runWithCMD(appId, appName, getMainClass(), args, onlyCompiled, pause);
	}
	
	/**
	 * ensure your program boots up with a command prompt terminal either a native configurable os terminal or JConsole.
	 * If you hard code your main class it won't support wrappers like eclipe's jar in jar loader.
	 * if you have connections in jvm args close them before reboot !SelfCommandPrompt#hasJConsole()
	 * @Since 2.0.0
	 */
	public static String[] runWithCMD(String appId, String appName, Class<?> mainClass, String[] args, boolean onlyCompiled, boolean pause) 
	{
		cacheApp(appId, appName, mainClass, args, pause);
		String bg = args.length != 0 ? args[0] : "";
		//run in the background if ordered to by an external process
		if(isBackground(bg))
		{
			background = bg;
			sameWindow = true;
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			return newArgs;
		}
		boolean compiled = isCompiled(mainClass);
		if(!compiled && onlyCompiled || compiled && System.console() != null || isDebugMode() || isWrapped())
		{
			sameWindow = true;
			return args;
		}
		
        if(hasJConsole())
        {
        	startJConsole(appId, appName);
        	return args;
        }
        
		try
		{
			rebootWithTerminal(appId, appName, mainClass, args, pause);
		}
		catch (IOException e)
		{
			startJConsole(appId, appName);
			e.printStackTrace();
		}
		return args;
	}

	/**
	 * reboot the program. config sync enabled
	 */
	public static void reboot() throws IOException
	{
		reboot(wrappedAppId, wrappedAppName, wrappedAppClass, wrappedAppArgs, wrappedPause);
	}
	
	/**
	 * reboot the program with new args. config sync enabled
	 */
	public static void reboot(String[] newArgs) throws IOException
	{
		reboot(wrappedAppId, wrappedAppName, wrappedAppClass, newArgs, wrappedPause);
	}
	
	/**
	 * reboot the program. config sync enabled
	 */
	public static void reboot(String appId, String appName, Class<?> mainClass, String[] args, boolean pause) throws IOException
	{
		syncConfig();
		if(hasJConsole() || sameWindow)
		{
			rebootNormally(mainClass, args);
		}
		else
		{
			rebootWithTerminal(appId, appName, mainClass, args, pause);
		}
	}

	/**
	 * reboot a java application normally with original args
	 * @since 2.1.1
	 */
	public static void rebootNormally(Class<?> mainClass, String[] args) throws IOException 
	{
        String libs = System.getProperty("java.class.path");
        if(containsAny(libs, INVALID))
        	throw new RuntimeException("one or more LIBRARIES contains illegal parsing characters:(" + libs + "), invalid:" + INVALID);
        
		ExeBuilder builder = new ExeBuilder();
		builder.addCommand("java");
		builder.addCommand(getJVMArgs());
		builder.addCommand("-cp");
		String q = OSUtil.getQuote();
		builder.addCommand(q + libs + q);//doesn't need to check parsing chars cause this is a generic reboot and if it reboots with terminal it will catch the error on boot
		builder.addCommand(mainClass.getName());
		builder.addCommand(programArgs(args));
		String command = builder.toString();
		runInTerminal(command);
		shutdown();
	}

	/**
	 * do not call this directly without checks or it will just keep rebooting and only in the terminal. Doesn't call {@link SelfCommandPrompt#cacheApp(String, String, Class, String[], boolean)}
	 */
	public static void rebootWithTerminal(String appId, String appName, Class<?> mainClass, String[] args, boolean pause) throws IOException
	{
    	if(containsAny(appId, INVALID))
    		throw new RuntimeException("appId contains illegal parsing characters:(" + appId + "), invalid:" + INVALID);
    	
            String libs = System.getProperty("java.class.path");
            if(containsAny(libs, INVALID))
            	throw new RuntimeException("one or more LIBRARIES contains illegal parsing characters:(" + libs + "), invalid:" + INVALID);
            
            ExeBuilder builder = new ExeBuilder();
        	builder.addCommand("java");
        	builder.addCommand(getJVMArgs());
        	builder.addCommand("-cp");
        	String q = OSUtil.getQuote();
        	builder.addCommand(q + libs + q);
        	builder.addCommand(SelfCommandPrompt.class.getName());
        	builder.addCommand(String.valueOf(pause));
        	builder.addCommand(mainClass.getName());
        	builder.addCommand(programArgs(args));
        	String command = builder.toString();
        	runInNewTerminal(appId, appName, appId, command);
        	shutdown();
	}
	
	/**
	 * enforces it to run in the command prompt terminal as sometimes it doesn't work without it
	 */
	public static void runInTerminal(String command) throws IOException
	{
		Runtime.getRuntime().exec(terminal + " " + OSUtil.getExeAndClose() + " " + command);
	}
	
	/**
	 * runs a command in a new terminal window.
	 * the sh name is the file name you want the shell script stored. The appId is to locate your folder
	 * @Since 2.0.0
	 */
	public static void runInNewTerminal(String appId, String appName, String shName, String command) throws IOException
	{
        if(OSUtil.isWindows())
        {
        	Runtime.getRuntime().exec(terminal + " " + OSUtil.getExeAndClose() + " start " + "\"" + appName + "\" " + command);
        }
        else if(OSUtil.isMac())
        {
        	File appdata = getAppdata(appId);
        	File sh = new File(appdata, shName + ".sh");
        	List<String> cmds = new ArrayList<>();
        	cmds.add("#!/bin/bash");
        	cmds.add("set +v");//@Echo off
        	cmds.add("echo -n -e \"\\033]0;" + appName + "\\007\"");//Title
        	cmds.add("cd " + getProgramDir().getAbsolutePath().replaceAll(" ", "\\ "));//set the proper directory
        	cmds.add(command);//actual command
        	IOUtils.saveFileLines(cmds, sh, true);//save the file
        	IOUtils.makeExe(sh);//make it executable
        	
        	File launchSh = new File(appdata, "run.sh");
        	List<String> li = new ArrayList<>();
        	li.add("#!/bin/bash");
        	li.add("osascript -e \"tell application \\\"Terminal\\\" to do script \\\"" + sh.getAbsolutePath().replaceAll(" ", "\\\\ ") + "\\\"\"");
        	IOUtils.saveFileLines(li, launchSh, true);
        	IOUtils.makeExe(launchSh);
        	Runtime.getRuntime().exec(terminal + " " + OSUtil.getExeAndClose() + " " + launchSh.getAbsolutePath().replaceAll(" ", "\\ "));
        }
        else if(OSUtil.isLinux())
        {
        	File sh = new File(getAppdata(appId), shName + ".sh");
        	List<String> cmds = new ArrayList<>();
        	cmds.add("#!/bin/bash");
        	cmds.add("set +v");//@Echo off
        	cmds.add("echo -n -e \"\\033]0;" + appName + "\\007\"");//Title
        	cmds.add("cd " + getProgramDir().getAbsolutePath());//set the proper directory
        	cmds.add(command);//actual command
        	IOUtils.saveFileLines(cmds, sh, true);//save the file
        	IOUtils.makeExe(sh);//make it executable
        	Runtime.getRuntime().exec(terminal + " " + OSUtil.getLinuxNewWin() + " " + sh.getAbsolutePath());
        }
	}

	/**
	 * execute your command line jar without redesigning your program to use java.util.Scanner to take input
	 * @since 2.0.0-rc.7
	 */	
	public static String[] wrapWithCMD(String[] argsInit)
	{
		return wrapWithCMD("", argsInit);
	}
	
	/**
	 * execute your command line jar without redesigning your program to use java.util.Scanner to take input.
	 * escape sequences are \char to have actual quotes in the jvm args cross platform
	 * @since 2.0.0-rc.9
	 */	
	public static String[] wrapWithCMD(String msg, String[] argsInit)
	{
		String appId = suggestAppId();
		return wrapWithCMD(msg, appId, appId, argsInit);
	}
	
	/**
	 * execute your command line jar without redesigning your program to use java.util.Scanner to take input.
	 * escape sequences are \char to have actual quotes in the jvm args cross platform
	 * @since 2.0.0-rc.9
	 */		
	public static String[] wrapWithCMD(String msg, String appId, String appName, String[] argsInit)
	{
		return wrapWithCMD(msg, appId, appName, argsInit, false, true);
	}
	
	/**
	 * execute your command line jar without redesigning your program to use java.util.Scanner to take input.
	 * escape sequences are \char to have actual quotes in the jvm args cross platform
	 * @since 2.1.0
	 */	
	public static String[] wrapWithCMD(String msg, String appId, String appName, String[] argsInit, boolean onlyCompiled, boolean pause)
	{
		return wrapWithCMD(msg, appId, appName, getMainClass(), argsInit, onlyCompiled, pause);
	}
	
	/**
	 * execute your command line jar without redesigning your program to use java.util.Scanner to take input.
	 * escape sequences are \char to have actual quotes in the jvm args cross platform
	 * @since 2.0.1
	 */	
	public static String[] wrapWithCMD(String msg, String appId, String appName, Class<?> mainClass, String[] argsInit, boolean onlyCompiled, boolean pause)
	{
		argsInit = SelfCommandPrompt.runWithCMD(appId, appName, mainClass, argsInit, onlyCompiled, pause);
		boolean shouldScan = argsInit.length == 0;
		if(!msg.isEmpty() && shouldScan)
			System.out.print(msg);//don't enforce line feed
		
		return shouldScan ? parseCommandLine(scanner.nextLine()) : argsInit;
	}
	
	public static String[] parseCommandLine(String line)
	{
		return parseCommandLine(line, '\\', '"');
	}

	public static String[] parseCommandLine(String line, char esq, char q)
	{
		List<String> args = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		String previous = "";
		boolean quoted = false;
		String replaceEsq = esq == '\\' ? "\\\\" : "" + esq;
		for(int index = 0; index < line.length(); index++)
		{
			String character = line.substring(index, index + 1);
			String compare = character;
			
			//escape the escape sequence
			if(previous.equals("" + esq) && compare.equals("" + esq))
			{
				previous = "aa";
				compare = "aa";
			}
			
			boolean escaped = previous.equals("" + esq);
			
			if(!escaped && compare.equals("" + q))
				quoted = !quoted;
			
			if(!quoted && compare.equals(" "))
			{
				args.add(replaceAll(builder.toString(), q, "", esq).replaceAll(replaceEsq + q, "" + q));
				builder = new StringBuilder();
				previous = compare;
				continue;
			}
			builder.append(character);
			previous = compare;
		}
		if(!builder.toString().isEmpty())
			args.add(replaceAll(builder.toString(), q, "", esq).replaceAll(replaceEsq + q, "" + q));
		
		return DeDuperUtil.toArray(args, String.class);
	}

	public static String replaceAll(String str, char what, String with, char esq)
	{
		if(what == '§')
			throw new IllegalArgumentException("unsupported opperend:" + what);
		StringBuilder builder = new StringBuilder();
		String previous = "";
		for(int index = 0; index < str.length(); index++)
		{
			String character = str.substring(index, index + 1);
			if(previous.equals("" + esq) && character.equals("" + esq))
			{
				previous = "§";
				character = "§";
			}
			boolean escaped = previous.equals("" + esq);
			previous = character;
			if(!escaped && character.equals("" + what))
				character = with;
			builder.append(character);
		}
		return builder.toString();
	}
	
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId()
	{
		return suggestAppId(getMainClassName());
	}
	
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId(Class<?> clazz)
	{
		return suggestAppId(clazz.getName());
	}
	
	/**
	 * return the suggested appId based on the main class name
	 */
	public static String suggestAppId(String name)
	{
		return name.replaceAll("\\.", "/");
	}

	public static void shutdown()
	{
		System.gc();
		System.exit(0);
	}

	/**
	 * checks if the jar is compiled based on the main class 
	 */
	public static boolean isCompiled()
	{
		return isCompiled(getMainClass());
	}
	
	/**
	 * checks per class if the jar is compiled
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
	public static File getFileFromClass(Class<?> clazz) throws UnsupportedEncodingException, RuntimeException
	{
		String jarPath = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();//get the path of the currently running jar
		String fileName = URLDecoder.decode(jarPath, "UTF-8").substring(1);
		if(fileName.contains(INVALID))
			throw new RuntimeException("jar file contains invalid parsing chars:" + fileName);
		return new File(fileName);
	}
	
	public static List<String> getJVMArgs()
	{
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMxBean.getInputArguments();
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
	
	public static String[] programArgs(String[] args) 
	{
		//append the background arg if it doesn't exist
		if(background != null && (args.length == 0 || !isBackground(args[0])) )
		{
			String[] newArgs = new String[args.length + 1];
			newArgs[0] = background;
			System.arraycopy(args, 0, newArgs, 1, args.length);
			args = newArgs;
		}
		String q = OSUtil.getQuote();
		String esc = OSUtil.getEsc();
		for(int i=0;i<args.length; i++)
			args[i] =  q + args[i].replaceAll(q, esc + q) + q;//wrap the jvm args to the native terminal quotes and escape quotes
		return args;
	}
	
	public static boolean isBackground(String arg)
	{
		return arg.equals("background") || arg.equalsIgnoreCase("runInBackground") || arg.equalsIgnoreCase("runInTheBackground");
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
	
	//Start APP VARS_____________________________
	
	public static void cacheApp(String appId, String appName, Class<?> mainClass, String[] args, boolean pause) 
	{
		wrappedAppId = appId;
		wrappedAppName = appName;
		wrappedAppClass = SelfCommandPrompt.class.equals(mainClass) ? getClass(System.getProperty("selfcmd.mainclass")) : mainClass;
		wrappedAppArgs = args;
		wrappedPause = pause;
	}
	
	/**
	 * @return if the main class is SelfCommandPrompt
	 * @Since 2.0.0-rc.6
	 */
	public static boolean isWrapped() 
	{
		return SelfCommandPrompt.class.getName().equals(getMainClassName());
	}
	
	/**
	 * returns the appdata contained in %appdata%/SelfCommandPrompt/appId
	 */
	public static File getAppdata(String appId)
	{
		return new File(selfcmd, appId);
	}
	
	public static boolean hasJConsole() 
	{
		return useJConsole || OSUtil.isUnsupported() || jconsole != null;
	}

	public static String terminal;
	public static boolean useJConsole;
	/**
	 * syncs the global terminal and the boolean for useJConsole
	 */
	public static void syncConfig() 
	{
    	MapConfig cfg = new MapConfig(new File(selfcmd, "console.cfg"));
    	cfg.load();
    	//load the terminal string
    	String cfgTerm = cfg.get("terminal", "").trim();
    	if(cfgTerm.isEmpty() || !OSUtil.isTerminalValid(cfgTerm))
    	{
    		cfgTerm = OSUtil.getTerminal();//since it's a heavy process cache it to the config
    		cfg.set("terminal", cfgTerm);
    	}
    	terminal = cfgTerm;
    	
    	useJConsole= cfg.get("useJConsole", false);//if user prefers JConsole over natives
    	cfg.save();
	}

	//End APP VARS_________________________________
	
	public static String parseQuotes(String s, char lq, char rq) 
	{
		return parseQuotes(s, 0, lq, rq);
	}

	public static String parseQuotes(String s, int index, char lq, char rq)
	{
		StringBuilder builder = new StringBuilder();
		char prev = 'a';
		int count = 0;
		boolean hasQuote = hasQuote(s.substring(index, s.length()), lq);
		for(int i=index;i<s.length();i++)
		{
			String c = s.substring(i, i + 1);
			char firstChar = c.charAt(0);
			if(firstChar == '\\' && prev == '\\')
			{
				prev = '/';
				firstChar = '/';//escape the escape
			}
			boolean escaped = prev == '\\';
			if(hasQuote && !escaped && (count == 0 && c.equals("" + lq) || count == 1 && c.equals("" + rq)))
			{
				count++;
				if(count == 2)
					break;
				prev = firstChar;//set previous before skipping
				continue;
			}
			if(!hasQuote || count == 1)
			{
				builder.append(c);
			}
			prev = firstChar;//set the previous char here
		}
		return lq == rq ? builder.toString().replaceAll("\\\\" + lq, "" + lq) : builder.toString().replaceAll("\\\\" + lq, "" + lq).replaceAll("\\\\" + rq, "" + rq);
	}

	public static boolean hasQuote(String str, char lq) 
	{
		char prev = 'a';
		for(char c : str.toCharArray())
		{
			if(c == lq && prev != '\\')
				return true;
			prev = c;
		}
		return false;
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
	
	public static <T> T[] toArray(Collection<T> col, Class<T> clazz)
	{
	    @SuppressWarnings("unchecked")
		T[] li = (T[]) Array.newInstance(clazz, col.size());
	    int index = 0;
	    for(T obj : col)
	    {
	        li[index++] = obj;
	    }
	    return li;
	}
	
	public static String[] splitFirst(String str, char sep, char lquote, char rquote)
	{
		return split(str, 1, sep, lquote, rquote);
	}
	
	public static String[] split(String str, char sep, char lquote, char rquote) 
	{
		return split(str, -1, sep, lquote, rquote);
	}
	
	/**
	 * split with quote ignoring support
	 * @param limit is the amount of times it will attempt to split
	 */
	public static String[] split(String str, int limit, char sep, char lquote, char rquote) 
	{
		if(str.isEmpty())
			return new String[]{str};
		List<String> list = new ArrayList<>();
		boolean inside = false;
		int count = 0;
		for(int i = 0; i < str.length(); i += 1)
		{
			if(limit != -1 && count >= limit)
				break;
			String a = str.substring(i, i + 1);
			char firstChar = a.charAt(0);
			char prev = i == 0 ? 'a' : str.substring(i-1, i).charAt(0);
			boolean escape = prev == '\\';
			if(firstChar == '\\' && prev == '\\')
			{
				prev = '/';
				firstChar = '/';//escape the escape
			}
			if(!escape && (a.equals("" + lquote) || a.equals("" + rquote)))
			{
				inside = !inside;
			}
			if(a.equals("" + sep) && !inside)
			{
				String section = str.substring(0, i);
				list.add(section);
				str = str.substring(i + ("" + sep).length());
				i = -1;
				count++;
			}
		}
		list.add(str);//add the rest of the string
		return toArray(list, String.class);
	}
	
	public static boolean containsAny(String string, String invalid) 
	{
		if(string.isEmpty())
			return invalid.isEmpty();
		
		for(int i=0; i < string.length(); i++)
		{
			String s = string.substring(i, i + 1);
			if(invalid.contains(s))
			{
				return true;
			}
		}
		return false;
	}
	
	public static String inject(String str, char before, char toInject)
	{
		int index = str.indexOf(before);
		return index != -1 ? str.substring(0, index) + toInject + str.substring(index) : str;
	}
	
	public static Class<?> getClass(String name) 
	{
		try 
		{
			return Class.forName(name);
		} 
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
		return null;
	}
}