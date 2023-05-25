package jredfox.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

public class BlockingFileInputStream extends InputStream {

	public BufferedReader detector;
	public InputStream oldIn;
	
	public BlockingFileInputStream()
	{
		
	}
	
	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
