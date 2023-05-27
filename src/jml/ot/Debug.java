package jml.ot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import jml.ipc.pipes.WrappedPrintStream;
import jredfox.common.file.FileUtils;

public class Debug {

	public static void main(String[] args) throws IOException
	{
		File log = new File("log.txt").getAbsoluteFile();
		FileUtils.create(log);
		System.setOut(new WrappedPrintStream(System.out, new PrintStream(new FileOutputStream(log), true)));
		System.out.println("A TESTa");
		System.out.print("123\nakjfkajfa\nLine Test:");
		System.out.println(true);
		System.out.println((byte)0);
		System.out.println((int)1);
		System.out.println((short)2);
		System.out.println((long)3);
		System.out.println('a');
		System.out.println(4.0F);
		System.out.println((double)5.0D);
		System.out.println("ChAras".toCharArray());
	}

}
