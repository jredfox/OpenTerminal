package jml.ot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		File fileIn = new File("in.txt").getAbsoluteFile();
		fileIn.getParentFile().mkdirs();
		fileIn.createNewFile();
		System.setIn(new BufferedInputStream(new FileInputStream(fileIn)));
//		while(true)
//		{
			if(System.in.available() > 0)
				copy("@OT.IN", System.in, System.out);
//		}
	}

	public static void copy(String ignore, InputStream in, OutputStream out) throws IOException
	{
		int c = 0;//flush counter
		
		//ignore flags
		String m = "";
		int sindex = 0;
		
		int b = in.read();
		while(b != -1)
		{
			if(ignore.charAt(sindex) == (char)b)
			{
				m += (char)b;
				sindex++;
				if(m.equals(ignore))
				{
					//TODO:
				}
				b = in.read();//ensure read increments
				continue;
			}
			else
			{
				if(!m.isEmpty())
				{
					out.write(m.getBytes());//write the characters to the output that we confirmed the ignore string wasn't there
				}
				sindex = 0;//reset data
				m = "";
			}
			
			//do the actual copying from in to out
			out.write(b);
			b = in.read();
			
			//auto flush every 4,000 bytes
			c++;
			if(c >= 4000)
			{
				out.flush();
				c = 0;
			}
		}
		//ensure it flushes even without any newline and it's a printstream
		out.flush();
	}

}
