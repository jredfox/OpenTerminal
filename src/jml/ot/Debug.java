package jml.ot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import jredfox.common.io.IOUtils;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		File fileIn = new File("in.txt").getAbsoluteFile();
		fileIn.getParentFile().mkdirs();
		fileIn.createNewFile();
		System.out.println(fileIn);
		System.setIn(new BufferedInputStream(new FileInputStream(fileIn)));
		BufferedReader in = IOUtils.getReader(System.in);
		while(true)
		{
			String s = in.readLine();
			if(s != null)
				System.out.println("input detected:" + s);
		}
	}

}
