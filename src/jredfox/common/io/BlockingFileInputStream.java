package jredfox.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class BlockingFileInputStream extends InputStream {

	public BufferedReader reader;
	public InputStream input;
	
	public BlockingFileInputStream(InputStream i)
	{
		this(IOUtils.getReader(i), i);
	}
	
	/**
	 * make sure that the reader's inputstream is the same as the inputstream param
	 */
	public BlockingFileInputStream(BufferedReader r, InputStream i)
	{
		this.reader = r;
		this.input = i;
	}
	
	@Override
	public int read() throws IOException 
	{
		// TODO Auto-generated method stub
		return 0;
	}

}
