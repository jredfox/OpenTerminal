package jml.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jredfox.common.Validate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReflectEnum {
	
	public static Object factory;
	public static Method ctrAccessor;
	public static Method ctrNewInstance;
	public static Method fieldAccessor;
	public static Method fieldAccessorSet;
	public static Field enumConstants = ReflectionHandler.getField(Class.class, "enumConstants");
    public static Field enumConstantDirectory = ReflectionHandler.getField(Class.class, "enumConstantDirectory");
	public static Field ordinal = ReflectionHandler.getField(Enum.class, "ordinal");
    
	static
	{
		try
		{
			Class classReflectFactory = ReflectionHandler.getClass("sun.reflect.ReflectionFactory");
			Class classCtrAcess = ReflectionHandler.getClass("sun.reflect.ConstructorAccessor");
			Class classFieldAcess = Class.forName("sun.reflect.FieldAccessor");
			
			factory = ReflectionHandler.getMethod(classReflectFactory, "getReflectionFactory").invoke(null);
			ctrAccessor = ReflectionHandler.getMethod(classReflectFactory,"newConstructorAccessor", Constructor.class);
			ctrNewInstance = ReflectionHandler.getMethod(classCtrAcess, "newInstance", Object[].class);
		
			fieldAccessor = ReflectionHandler.getMethod(classReflectFactory, "newFieldAccessor", Field.class, boolean.class);
			fieldAccessorSet = ReflectionHandler.getMethod(classFieldAcess, "set", Object.class, Object.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static <T extends Enum> T getEnum(Class<? extends Enum> clazz, String name)
    {
    	try
    	{
    		for(Enum e : clazz.getEnumConstants())
    		{
    			if(e.name().equals(name))
    				return (T) e;
    		}
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
	/**
	 * get an enum based on it's ordinal(index)
	 */
	public static <T extends Enum> T getEnum(Class<? extends Enum> clazz, int ordinal)
    {
    	try
    	{
    		return (T) clazz.getEnumConstants()[ordinal];
    	}
    	catch(ArrayIndexOutOfBoundsException e)
    	{
    		
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
	
    public static boolean containsEnum(Class<? extends Enum> clazz, String name)
    {
    	return getEnum(clazz, name) != null;
    }
    
    /**
     * internal do not use please
     * enum constructors are String.class, int.class plus and whatever is in your enum constructor
     */
    public static Constructor getEnumConstructor(Class<? extends Enum> clazz, Class... params)
    {
        Class<?>[] corrected = new Class[params.length + 2];
        corrected[0] = String.class;
        corrected[1] = Integer.class;
        System.arraycopy(params, 0, corrected, 2, params.length);//copy the contents of params into corrected starting at index 2
    	return ReflectionHandler.getWrappedConstructor(clazz, corrected);
    }
    
    /**
     * internal do not use. Use only if grabbing the regular constructors new instance fails to instantiate the object
     */
    @Deprecated
    public static Object getCtrAccessor(Constructor ctr) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
    {
    	return ctrAccessor.invoke(factory, ctr);
	}
    
    @Deprecated
    public static Object getFieldAcessor(Field field) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
    	return fieldAccessor.invoke(factory, field, false);
    }
    
    /**
     * create an enum without adding it to memory
     */
    public static <T extends Enum> T createEnum(Class<? extends Enum> clazz, String enumName, Object... params)
    {
    	try
    	{
    		//grab the enum constructor
    		Constructor ctr = getEnumConstructor(clazz, getParams(params));
    		
    		//correct the enum parameters
    		Object[] corrected = new Object[params.length + 2];
    		corrected[0] = enumName;
    		corrected[1] = -1;//leave the ordinal as -1 till the enum gets added into later
    		System.arraycopy(params, 0, corrected, 2, params.length);
    		
    		Object accessor = getCtrAccessor(ctr);
    		Enum e = (Enum)ctrNewInstance.invoke(accessor, new Object[]{corrected});
    		return (T) clazz.cast(e);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * add a synthetic enums into memory
     */
	public static <T extends Enum> void addEnum(T... enums)
    {
    	try
    	{
			Class<? extends Enum> clazz = enums[0].getClass();
    		sanityEnumCheck(enums);
    		Field fieldValues = getEnumHolder(clazz);
    		
    		//copy new enums to the field
    		Enum[] oldValues = (Enum[]) fieldValues.get(null);
    		Enum[] newValues = (Enum[]) Array.newInstance(clazz, oldValues.length + enums.length);
    		System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
    		int index = oldValues.length;
    		System.arraycopy(enums, 0, newValues, index, enums.length);
    		for(Enum e : enums)
    			setOrdinal(e, index++);

    		setField(fieldValues, null, newValues);
    		clearEnumCache(clazz);
    	}
    	catch(Throwable t)
    	{
    		t.printStackTrace();
    	}
    }
	
	private static void sanityEnumCheck(Enum[] enums) 
    {
    	Class clazz = enums[0].getClass();
    	Set<String> ids = new HashSet<String>();
		for(Enum e : enums)
		{
			Validate.nonNull(e);
			String name = e.name();
			if(!e.getClass().equals(clazz))
				throw new RuntimeException("enum class:" + clazz.getName() + " doesn't match the type of:" + e.getClass());
			else if(ReflectEnum.containsEnum(clazz, name) || ids.contains(name))
				throw new RuntimeException("Duplicate enum name variable!:" + e.name());
			ids.add(e.name());
		}
	}
	
	private static Field getEnumHolder(Class<? extends Enum> clazz)
	{
		Field fieldValues = ReflectionHandler.getField(clazz, "$VALUES", "ENUM$VALUES");
		if(fieldValues == null)
		{
	        boolean isDeob = true;//TODO: fix later
			int flags = (isDeob  ? Modifier.PUBLIC : Modifier.PRIVATE) | Modifier.STATIC | Modifier.FINAL | 0x1000 /*SYNTHETIC*/;
	        if (fieldValues == null)
	        {
	            String valueType = String.format("[L%s;", clazz.getName().replace('.', '/'));
	            for (Field field : clazz.getDeclaredFields())
	            {
	                if ((field.getModifiers() & flags) == flags &&
	                     field.getType().getName().replace('.', '/').equals(valueType)) //Apparently some JVMs return .'s and some don't..
	                {
	                    fieldValues = field;
	                    break;
	                }
	            }
	        }
		}
		return fieldValues;
	}
    
    private static void setField(Field f, Object instance, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
    {
		Object fa = getFieldAcessor(f);
		fieldAccessorSet.invoke(fa, instance, value);
	}
    
    private static void clearEnumCache(Class<? extends Enum> clazz) 
    {
		ReflectionHandler.set(enumConstants, clazz, null);
		ReflectionHandler.set(enumConstantDirectory, clazz, null);
	}

    public static void setOrdinal(Enum e, int index) 
    {
    	ReflectionHandler.set(ordinal, e, index);
    	Validate.isTrue(e.ordinal() == index);
	}

	private static Class[] getParams(Object[] params) 
    {
    	Class[] c = new Class[params.length];
    	for(int i = 0; i < params.length; i++)
    		c[i] = params[i].getClass();
		return c;
	}

	/**
	 * removes an enum from in memory based on ordinal id
	 */
	public static <T extends Enum<? >> void removeEnum(Enum... toRemove)
    {
		try
		{
			Class<? extends Enum> clazz = toRemove[0].getClass();
			sanityCheckRemove(toRemove);
			Field fieldValues = getEnumHolder(clazz);
			Enum[] oldValues = (Enum[]) fieldValues.get(null);
			//remove all the ones I need to remove
			for(Enum eh : toRemove)
				oldValues[eh.ordinal()] = null;
			//create a new list that's trimed around it
			List<Enum> list = new ArrayList<Enum>();
			for(Enum old : oldValues)
			{
				if(old != null)
					list.add(old);
			}
			int index = 0;
			for(Enum e : list)
				setOrdinal(e, index++);
			
			setField(fieldValues, null, list.toArray((T[]) Array.newInstance(clazz, 0)));
    		clearEnumCache(clazz);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
    }

	private static void sanityCheckRemove(Enum[] toRemove) 
	{
		for(Enum e : toRemove)
		{
			if(e.ordinal() < 0)
				throw new RuntimeException("ordinal not set:" + e + " the enum is probably not in memory call EnumReflect#addEnum(enum)");
			Enum check = getEnum(e.getClass(), e.ordinal());
			if(check == null)
				throw new IllegalArgumentException("enum already removed:" + e);
			else if(check != e)
				throw new RuntimeException("enum mismatch:" + e + " index:" + check);
		}
	}

}
