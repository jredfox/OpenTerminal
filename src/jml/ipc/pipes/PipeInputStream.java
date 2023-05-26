package jml.ipc.pipes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import jredfox.common.utils.JREUtil;

/**
 * PipedInputStream is a class to treat a File like it's a regular Pipe(NonNamed
 * or Named) as it hangs when read() is called and there is no more data
 * If you use BufferedReader Scanner or BufferedInputStream you must have a new line feed for every input received
 * @author jredfox
 */
public class PipeInputStream extends FileInputStream
{
	public long msTime;
	public String signal;
	public PrintStream out;

	public PipeInputStream(File file, String signal, PrintStream out) throws FileNotFoundException
	{
		this(file, signal, out, 50);
	}

	public PipeInputStream(File file, String signal, PrintStream out, long mt) throws FileNotFoundException 
	{
		super(file);
		this.msTime = mt;
	}

	/**
	 * read but blocking if it's the EOS
	 */
	@Override
	public int read() throws IOException
	{
		 int b = super.read();
//		 if(b == -1 && this.signal != null)
//		 {
//			 this.out.write(this.signal);
//			 this.out.flush();//TODO:
//		 }
		 while (b == -1)
		 {
			 b = super.read();
			 JREUtil.sleep(this.msTime);
		 }
		 return b;
	}

	@Override
	public int read(byte b[]) throws IOException 
	{
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException 
	{
		int bytes_read = read1(b, off, len);
		
		//if 0 bytes are read we have to wait till it reads at least 1 byte
		while(bytes_read < 1)
		{
			bytes_read = read1(b, off, len);
			JREUtil.sleep(this.msTime);
		}
		
		return bytes_read;
	}

	public int read1(byte[] b, int off, int len) throws IOException 
	{
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = super.read();
        if (c == -1) {
            return -1;
        }
        
        b[off] = (byte)c;

        int i = 1;
        try {
            for (; i < len ; i++) {
                c = super.read();
                if (c == -1) {
                    break;
                }
                b[off + i] = (byte)c;
            }
        } catch (IOException ee) {
        }
        return i;
	}
}
