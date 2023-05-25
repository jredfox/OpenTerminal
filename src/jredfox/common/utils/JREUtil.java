package jredfox.common.utils;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import jml.ot.OpenTerminal;
import jml.ot.TerminalUtil;

public class JREUtil {
	
	public static final String INVALID = "\"'`,";
	
	static
	{	
		System.setProperty("runnables.jar", getFileFromClass(getMainClass()).getPath());
		if(System.getProperty("user.appdata") == null)
		{
			System.setProperty("user.appdata", TerminalUtil.getAppData().getPath());
		}
		JREUtil.patchDir();//patch os's screwing up initial directory untested, patch macOs java launcher returning junk //TODO: test make sure it works
	}

	/**
	 * Must be called before OpenTerminal#run(TerminalApp app)
	 * NOTE: calling this forces the user directory regardless of os. this changes behavior on linux where user.dir = user.home
	 */
	public static void syncUserDirWithJar()
	{
		setUserDir(new File(System.getProperty("runnables.jar")).getParentFile());
	}
	
	/**
	 * patch macOs
	 */
	public static void patchDir()
	{
		String dir = System.getProperty("user.dir");
		String tmp = System.getProperty("java.io.tmpdir");
		if(dir.contains(tmp) && !dir.startsWith(tmp))
			setUserDir(TerminalUtil.isLinux() ? new File(System.getProperty("user.home")) : new File(System.getProperty("runnables.jar")).getParentFile());
	}

	/**
	 * must be called before {@link OpenTerminal#runWithCMD(String, String, Class, String[], boolean, boolean)}
	 * @param file
	 */
	public static void setUserDir(File file)
	{
		System.setProperty("user.dir", file.getPath());
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
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(String name, boolean print) 
	{
		try 
		{
			return (Class<T>) Class.forName(name);
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
	 * shut down your java application
	 */
	public static void shutdown()
	{
		shutdown(0);
	}
	
	/**
	 * shut down your java application
	 */
	public static void shutdown(int code)
	{
		System.gc();
		System.exit(code);
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
	
	/**
	 * cause a thread to sleep garenteed even with thread interuptions if boolean is true
	 */
	public static void sleep(long time)
	{
		sleep(time, true);
	}

	/**
	 * cause a thread to sleep to for time in ms. If noInterupt it won't allow interuptions to stop the sleep
	 */
	public static void sleep(long time, boolean noInterupt)
	{
		long startMs = System.currentTimeMillis();
		try 
		{
			Thread.sleep(time);
		}
		catch (InterruptedException | IllegalArgumentException e) 
		{
			if(noInterupt)
			{
				long current = System.currentTimeMillis();
				long passedMs = current - startMs;
				time = time - passedMs;
				long stopMs = System.currentTimeMillis() + time;
				System.err.println("causing manual sleep due to interuption for:" + time);
				while(System.currentTimeMillis() < stopMs)
				{
					;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz) 
	{
		try
		{
			return (T) clazz.newInstance();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<?> clazz, Class<?>[] ids, Object... params)
	{
		try 
		{
			return (T) clazz.getConstructor(ids).newInstance(params);
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
		return null;
	}

	public static void clearProperty(String s) 
	{
		try
		{
			System.clearProperty(s);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static Class[] getWrappedClasses(Class[] params) 
    {
		for(int i=0; i< params.length; i++)
		{
			params[i] = getWrappedClass(params[i]);
		}
		return params;
	}
	
	public static Class getWrappedClass(Class clazz) 
    {
    	if(clazz.isPrimitive())
    	{
    		if(boolean.class.equals(clazz))
    			return Boolean.class;
    		else if(char.class.equals(clazz))
    			return Character.class;
    		else if(byte.class.equals(clazz))
    			return Byte.class;
    		else if(short.class.equals(clazz))
    			return Short.class;
    		else if(int.class.equals(clazz))
    			return Integer.class;
    		else if(long.class.equals(clazz))
    			return Long.class;
    		else if(float.class.equals(clazz))
    			return Float.class;
    		else if(double.class.equals(clazz))
    			return Double.class;
    		else
    			return null;//unkown data type
    	}
		return clazz;
	}
	
    public static boolean equals(Class[] compare, Class[] params)
    {
    	if(compare.length != params.length)
			return false;
		for(int i=0;i<params.length;i++)
		{
			Class c1 = params[i];
			Class c2 = compare[i];
			if(!c1.equals(c2))
				return false;
		}
		return true;
	}
    
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
       getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
   }

    protected static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

           for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
         }
     }


}
