package jml.ipc.pipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jredfox.common.utils.JREUtil;

/**
 * PipedInputStream is a class to treat a File like it's a regular
 * Pipe(NonNamed or Named) as it hangs when read() is called and there is no
 * more data
 * @author jredfox
 */
public class PipeInputStream extends FileInputStream
{
	public long msTime;
	public PipeInputStream(File file) throws FileNotFoundException 
	{
		this(file, 50);
	}
	
	public PipeInputStream(File file, long mt) throws FileNotFoundException 
	{
		super(file);
		this.msTime = mt;
	}
	
	@Override
	public int read() throws IOException
	{
		return this.read(true);
	}

	/**
	 * read but pausing if it's the end of the stream
	 */
	public int read(boolean blocking) throws IOException
	{
		int b = super.read();
		if(blocking)
		{
			while (b == -1)
			{
				b = super.read();
				JREUtil.sleep(this.msTime);
			}
		}
		return b;
	}
	
	/**
	 * do default inputstream things when they attempt to buffer everything. BufferedInputStream still breaks with FileInputStream
	 */
//	@Override
//	public int read(byte b[], int off, int len) throws IOException
//	{
//        if (b == null) {
//            throw new NullPointerException();
//        } else if (off < 0 || len < 0 || len > b.length - off) {
//            throw new IndexOutOfBoundsException();
//        } else if (len == 0) {
//            return 0;
//        }
//
//        int c = read(false);
//        if (c == -1) {
//            return -1;
//        }
//        b[off] = (byte)c;
//
//        int i = 1;
//        try {
//            for (; i < len ; i++) {
//                c = read(false);
//                if (c == -1) {
//                    break;
//                }
//                b[off + i] = (byte)c;
//            }
//        } catch (IOException ee) {
//        }
//        return i;
//	}
}
