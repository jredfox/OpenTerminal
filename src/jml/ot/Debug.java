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
		System.out.println(new Debug().getClass().getName());
	}
	
}
