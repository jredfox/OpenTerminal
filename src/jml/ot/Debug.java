package jml.ot;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		File fileIn = new File("in.txt").getAbsoluteFile();
		fileIn.getParentFile().mkdirs();
		fileIn.createNewFile();
		System.setIn(new BufferedInputStream(new FileInputStream(fileIn)));
		int b = System.in.read();
		while(b != -1)
		{
			System.out.write(b);
			b = System.in.read();
		}
		System.out.flush();
//		BufferedReader in = IOUtils.getReader(System.in);
//		while(true)
//		{
//			String s = in.readLine();
//			while(s != null)
//			{
//				s = in.readLine();
//			}
//		}
//		String tst = "💜abcd";
//		byte[] arr = tst.getBytes();
//		for(byte b : arr)
//		{
//			System.out.write(b);
//		}
//		System.out.flush();
	}

}
