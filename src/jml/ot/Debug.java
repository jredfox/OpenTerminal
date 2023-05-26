package jml.ot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import jml.ipc.pipes.PipeInputStream;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		System.out.println("starting");
		File fileIn = new File("in.txt").getAbsoluteFile();
		fileIn.getParentFile().mkdirs();
		fileIn.createNewFile();
		InputStream in = new PipeInputStream(fileIn);
		System.setIn(in);
//		System.setIn(new FileInputStream(fileIn));
		try
		{
			Scanner scanner = new Scanner(System.in);
			System.out.println(scanner.nextLine());
			System.out.println(scanner.nextLine());
//			BufferedReader reader = IOUtils.getReader(System.in);
//			System.out.println(reader.readLine());
//			System.out.println(reader.readLine());
//			System.out.println(reader.readLine());
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
//		System.setIn(new BufferedInputStream(new PipeInputStream(fileIn)));
//		int b = System.in.read();
//		while(b != -1)
//		{
//			System.out.print(b + ",");
//			System.out.flush();
//			b = System.in.read();
//		}
//		System.out.flush();
//		while(true)
//		{
//			String l = reader.readLine();
//			while(l != null)
//			{
//				System.out.println(l);
//				l = reader.readLine();
//			}
//		}
	}

}
