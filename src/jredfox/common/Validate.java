package jredfox.common;

public class Validate {
	
	public static void nonNull(Object obj)
	{
		if(obj == null)
			throw new NullPointerException();
	}
	
	public static void isNull(Object obj)
	{
		if(obj != null)
			throw new IllegalStateException("Object is not null"); 
	}
	
	public static void isFalse(Boolean b)
	{
		if(b.booleanValue())
			throw new IllegalStateException("Boolean is true");
	}
	
	public static void isTrue(Boolean b)
	{
		if(!b.booleanValue())
			throw new IllegalStateException("Boolean is false");
	}

}
