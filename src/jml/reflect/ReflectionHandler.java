package jml.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.Validate;
import jredfox.common.utils.JREUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReflectionHandler {
	
	public static Field modifiersField;
	static
	{
		try
		{
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}
	public static Field elementData = ReflectionHandler.getField(ArrayList.class, "elementData");
	
    public static Field getField(Class clazz, MCPSidedString mcp)
    {
    	return getField(clazz, mcp.toString());
    }
	
	/**
	 * makes the field public and strips the final modifier
	 */
    public static Field getField(Class clazz, String... names)
    {
    	for(String name : names)
    	{
    		try
    		{
    	    	Field field = clazz.getDeclaredField(name);
    			field.setAccessible(true);
    			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    			return field;
    		}
    		catch(NoSuchFieldException e)
    		{
    			
    		}
    		catch(Throwable t)
    		{
    			t.printStackTrace();
    		}
    	}
        return null;
    }

	public static Method getMethod(Class clazz, MCPSidedString mcp)
    {
    	return getMethod(clazz, mcp.toString());
    }
    
    public static Method getMethod(Class clazz, String name, Class... params)
    {
        try
        {
            Method method = clazz.getDeclaredMethod(name, params);
            method.setAccessible(true);
            return method;
        }
        catch(NoSuchMethodException e)
        {
        	
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return null;
    }
    
    public static <T> T getStatic(Field field)
    {
    	return get(field, null);
    }
    
    public static <T> T get(Field field, Object instance)
    {
    	try
    	{
			return (T) field.get(instance);
		} 
    	catch (Throwable t)
    	{
			t.printStackTrace();
		} 
    	return null;
    }
    
    public static void setStatic(Field field, Object toset)
    {
    	set(field, null, toset);
    }
    
    public static void set(Field field, Object instance, Object toSet)
    {
    	try 
    	{
			field.set(instance, toSet);
		} 
    	catch (Throwable t)
    	{
			t.printStackTrace();
		}
    }
    
    public static <T> T invokeStatic(Method method, Object... params)
    {
    	return invoke(method, null, params);
    }
    
    public static <T> T invoke(Method method, Object instance, Object... params)
    {
    	try
    	{
    		return (T) method.invoke(instance, params);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    public static Constructor getConstructor(Class clazz, Class... params)
    {
    	try
    	{
    		Constructor ctr =  clazz.getDeclaredConstructor(params);
    		ctr.setAccessible(true);
    		return ctr;
    	}
    	catch(NoSuchMethodException e)
    	{
    		
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
		return null;
    }
    
    /**
     * convert all primitive classes I check into their wrapper classes
     */
    public static Constructor getWrappedConstructor(Class clazz, Class... params)
    {
    	try
    	{
    		JREUtil.getWrappedClasses(params);
    		Constructor[] ctrs = clazz.getDeclaredConstructors();
    		for(Constructor ctr : ctrs)
    		{
    			Class[] compare = JREUtil.getWrappedClasses(ctr.getParameterTypes());
    			if(JREUtil.equals(compare, params))
    				return ctr;
    		}
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    public static <T> Class<T> getClass(String className)
    {
    	try
    	{
    		return (Class<T>) Class.forName(className);
    	}
    	catch(ClassNotFoundException e)
    	{
    		
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    
    public static <T> Class<T> getClass(String name, boolean clinit, ClassLoader loader)
    {
    	try
    	{
    		return (Class<T>) Class.forName(name, clinit, loader);
    	}
    	catch(ClassNotFoundException e)
    	{
    		
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    public static <T> Class<T> getArrayClass(T[] arr)
    {
    	return getArrayClass((Class<T>) arr.getClass());
    }
    
    public static <T> Class<T> getArrayClass(Class<T> clazz)
    {
    	Validate.isTrue(clazz.isArray());
    	return (Class<T>) clazz.getComponentType();
    }
    
    public static <T> T cast(Class<T> clazz, Object tocast)
    {
    	return (T) clazz.cast(tocast);
    }
    
    public static ClassLoader getClassLoader(Class clazz)
    {
    	return clazz.getClassLoader();
    }
    
    public static boolean instanceOf(Class base, Class compare)
    {
    	return base.isAssignableFrom(compare);
    }
    
    public static boolean instanceOf(Class base, Object obj)
    {
    	return base.isInstance(obj);
    }
    
    public static <T> T newInstance(Class<T> clazz)
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
    
    public static <T extends Annotation> T getAnnotation(Class clazz, Class<T> annot)
    {
    	try
    	{
    		return (T) clazz.getDeclaredAnnotation(annot);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    public static Class<? extends Annotation> getAnnotationClass(Annotation an)
    {
    	return an.annotationType();
    }
    
    public static boolean instanceOfInterface(Class clazz, Class intf)
    {
    	try
    	{
    		Validate.isTrue(intf.isInterface());
    		for(Class c : getInterfaces(clazz))
    		{
    			if(instanceOf(intf, c))
    				return true;
    		}
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return false;
    }
    
    public static List<Class<?>> getInterfaces(Class clazz)
    {
    	List<Class<?>> clazzes = JREUtil.getAllInterfaces(clazz);
    	if(clazz.isInterface() && !clazzes.contains(clazz))
    		clazzes.add(clazz);//check to make sure the library didn't update and fix this bug
    	return clazzes;
    }
    
    /**
     * Wouldn't recommend using this directly use newEnum instead. Then later instantiate them into class enum memory
     */
    public static <T extends Enum> T addEnum(Class<? extends Enum> clazz, String name, Object... params)
    {
    	Enum e = ReflectEnum.createEnum(clazz, name, params);
    	ReflectEnum.addEnum(e);
    	return (T) e;
    }
    
    /**
     * create an enum without instantiating it into class enum arrays
     */
    public static <T extends Enum> T newEnum(Class<? extends Enum> clazz, String name, Object... params)
    {
    	Enum e = ReflectEnum.createEnum(clazz, name, params);
    	return (T) e;
    }
    
    public static <T extends Enum> void addEnum(T... enums)
    {
    	ReflectEnum.addEnum(enums);
    }
    
    public static boolean containsEnum(Class<? extends Enum> clazz, String name)
    {
    	return ReflectEnum.containsEnum(clazz, name);
    }
    
    public static <T extends Enum> T getEnum(Class<? extends Enum> clazz, String name)
    {
    	return (T) ReflectEnum.getEnum(clazz, name);
    }
    
    public static <T> T[] newArray(Class<T> clazz, int size)
    {
    	return (T[]) Array.newInstance(clazz, size);
    }
    
    public static <T> int capacity(ArrayList<T> list)
    {
       return ((T[]) ReflectionHandler.get(elementData, list)).length;
    }
    
	/**
     * if your java security for some reason is high call this method each time before using a field in reflection
     * REPORT IT AS A BUG TO ME IF YOU EVER NEED TO USE THIS!
     */
    public static void makeAccessible(Field field) throws Exception 
    {
        field.setAccessible(true);
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
}
