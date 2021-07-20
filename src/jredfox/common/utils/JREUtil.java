package jredfox.common.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import jredfox.common.thread.ShutdownThread;

public class JREUtil {
	
	public static final String INVALID = "\"'`,";
	
	static
	{	
		System.setProperty("runnables.jar", getFileFromClass(getMainClass()).getPath());
		
		//patch macOs returning junk #untested before Big Sur
		String dir = System.getProperty("user.dir");
		String tmp = System.getProperty("java.io.tmpdir");
		if(dir.contains(tmp) && !dir.startsWith(tmp))
			JREUtil.syncUserDirWithJar();
	}

	/**
	 * Must be called before {@link SelfCommandPrompt#runWithCMD(String, String, Class, String[], boolean, boolean)}
	 * Incompatible with Eclipe's jar in jar loader rn
	 */
	public static void syncUserDirWithJar()
	{
		try 
		{
			setUserDir(new File(System.getProperty("runnables.jar")).getParentFile());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * must be called before {@link SelfCommandPrompt#runWithCMD(String, String, Class, String[], boolean, boolean)}
	 * @param file
	 */
	public static void setUserDir(File file)
	{
		System.setProperty("user.dir", file.getAbsolutePath());
	}

	/**
	 *  optimized method for checking if the main executing jar isCompiled
	 */
	public static boolean isCompiled()
	{
		return System.getProperty("runnables.jar").endsWith(".jar");
	}
	
	/**
	 * checks per class if the jar is compiled
	 */
	public static boolean isCompiled(Class<?> mainClass)
	{
		try 
		{
			return getFileFromClass(mainClass).getName().endsWith(".jar");
		}
		catch (RuntimeException e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isDebugMode()
	{
		return java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
	}
	
	/**
	 * get a file from a class
	 * @throws URISyntaxException 
	 */
	public static File getFileFromClass(Class<?> clazz) throws RuntimeException
	{
		clazz = isEclipseJIJ() ? loadSyClass(clazz.getName(), false) : clazz;
		URL jarURL = clazz.getProtectionDomain().getCodeSource().getLocation();//get the path of the currently running jar
		File file = FileUtil.getFile(jarURL);
		String fileName = file.getPath();
		if(fileName.contains(INVALID))
			throw new RuntimeException("jar file contains invalid parsing chars:" + fileName);
		return file;
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
	
	
	private static Class<?> JIJ = getClass("org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader", false);
	public static boolean isEclipseJIJ()
	{
		return JIJ != null || getMainClassName().endsWith("jarinjarloader.JarRsrcLoader");
	}
	
	public static Class<?> loadSyClass(String name, boolean init)
	{
		try 
		{
			return Class.forName(name, init, ClassLoader.getSystemClassLoader());
		} 
		catch (Throwable t)
		{
			t.printStackTrace();
		}
		return null;
	}
	
	public static Class<?> getClass(String name, boolean print) 
	{
		try 
		{
			return Class.forName(name);
		} 
		catch(ClassNotFoundException c)
		{
			if(print)
				c.printStackTrace();
		}
		catch (Throwable t) 
		{
			t.printStackTrace();
		}
		return null;
	}
	
	/**
	 * NOTE: this isn't a shutdown event to prevent shutdown only a hook into the shutdown events. 
	 * That would be app specific this is jvm program (non app) specific which works for both
	 */
	public static void addShutdownThread(ShutdownThread sht)
	{
		throw new RuntimeException("Unsupported Check back in a future version!");
	}
	
	public static void shutdown()
	{
		System.gc();
		System.exit(0);
	}

	public static File getProgramDir() 
	{
		return new File(System.getProperty("user.dir"));
	}
	
	public static List<String> getJVMArgs()
	{
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMxBean.getInputArguments();
	}


}
