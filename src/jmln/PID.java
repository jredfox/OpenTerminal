package jmln;

import java.io.File;

public class PID {
	
	static
	{
		System.load(new File("C:\\Users\\jredfox\\Documents\\dev\\natives\\PIDIA\\src\\native.dll").getAbsolutePath());
	}
	
	public static native void l();

}
