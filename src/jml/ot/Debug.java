package jml.ot;

import java.util.ArrayList;
import java.util.List;

public class Debug {

	public static void main(String[] args)
	{
		String s = "\"command with spaces\" arg0 \"arg1 with\\\"quotes\\'\" arg\\t2 \"ar\"\"g3\"";
//		String s = "\"arg\"\"0";
//		System.out.println(s);
		String[] arr = parseCommand(s);
		for(String a : arr)
			System.out.println("\"" + a + "\"");
	}
	
	/**
	 * parse a command and turn it into arguments with escape sequencing supported
	 */
	public static String[] parseCommand(String cmd)
	{
		cmd = cmd.trim();
		List<String> arr = new ArrayList<>();
		StringBuilder b = new StringBuilder();
		boolean q = false;
		char slash = '\\';
		char q1 = '"';
		char q2 = '\'';
		char[] chars = cmd.toCharArray();
		char c = '.';
		char next = 'Z';
		for(int i=0; i < chars.length; i++)
		{
			c = chars[i];
			next = i+1 < chars.length ? chars[i+1] : 'Z';
			if(c == '\\' && (next == slash || next == q1 || next == q2))
			{
				b.append(next);
				i++;
				continue;
			}
			
			//set the quote boolean to preserve spacing
			if(c == q1 || c == q2)
			{
				//escape the double quotes after the variable has started
				if(q && c == next)
				{
					b.append(c);
					i++;//skips current loop and the next quote
					continue;
				}
				q = !q;
				continue;
			}
			
			//new variable detected
			if(!q && c == ' ')
			{
				arr.add(b.toString());
				b = new StringBuilder();
				continue;
			}
			
			//append the characters
			b.append(c);
		}
		//add the last arg
		String l = b.toString();
		if(!l.isEmpty())
			arr.add(l);
		return arr.isEmpty() ? new String[0] : arr.toArray(new String[0]);
	}
	
}
