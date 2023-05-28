package jredfox.common.utils;

/**
 * Assertion class to bring back assert without having to play with JVM arguments
 */
public class Assert {
	
	public static void is(boolean b, String e)
	{
		if(!b)
		   throw new AssertionError(e);
	}

	public static void is(boolean b) 
	{
		if(!b)
			throw new AssertionError();
	}
	
	public static void is(boolean b, Object o)
	{
		if(!b)
		   throw new AssertionError(o);
	}

}
