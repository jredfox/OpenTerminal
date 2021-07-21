package jredfox.common.utils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JavaUtil {
	
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
	
	public static String getLastSplit(String str, String sep) 
    {
        String[] arr = str.split(sep);
        return arr[arr.length - 1];
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
	
	public static String getExtensionFull(File file) 
	{
		String ext = FileUtil.getExtension(file);
		return ext.isEmpty() ? "" : "." + ext;
	}
	
	public static String inject(String str, char before, char toInject)
	{
		int index = str.indexOf(before);
		return index != -1 ? str.substring(0, index) + toInject + str.substring(index) : str;
	}
	
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

	public static <V> V getFirst(Map<?, V> m)
	{
		for(Object k : m.keySet())
		{
			return m.get(k);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> void setFirst(LinkedHashMap<K, V> map, V v) 
	{
		map.put((K) JavaUtil.getFirst(map), v);
	}

}
