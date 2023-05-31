package jredfox.common.utils;

/**
 * Assertion class to bring back assert without having to play with JVM arguments
 */
public class Assert {
	
	/**
	 * if you don't want Assertion errors you can disable them in memory but just know this is like creating Objects with illegal argument exceptions and hoping nothing crashes
	 */
	private static volatile boolean isActive = System.getProperty("noAssertions") == null;
	
	public static void is(boolean b, String e)
	{
		if(!b && isActive)
		   throw new AssertionError(e);
	}

	public static void is(boolean b) 
	{
		if(!b && isActive)
			throw new AssertionError();
	}
	
	public static void is(boolean b, Object o)
	{
		if(!b && isActive)
		   throw new AssertionError(o);
	}
	
	public static boolean getIsActive()
	{
		return isActive;
	}
	
	public static void setIsActive(boolean b)
	{
		isActive = b;
	}

}
