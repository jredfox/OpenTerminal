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
	/**
	 * how many MS the thread sleeps for when -1 is called from read()
	 */
	public long sleep;
	/**
	 * how many MS before a timeout occurs. -1 means it never timesout.
	 */
	protected long timeout = -1;
	/**
	 * send a msg if any to the outputstream provided that you are awaiting input from this stream
	 */
	public String signal;
	public OutputStream out;

	public PipeInputStream(File file) throws FileNotFoundException
	{
		this(file, PipeManager.REQUEST_INPUT, System.out);
	}

	public PipeInputStream(File file, String signal, PrintStream out) throws FileNotFoundException
	{
		this(file, signal, out, 50);
	}

	public PipeInputStream(File file, String signal, PrintStream out, long s) throws FileNotFoundException 
	{
		super(file);
		this.signal = signal;
		this.out = out;
		this.sleep = s;
	}

	/**
	 * read but blocking if it's the EOS
	 */
	@Override
	public int read() throws IOException
	{
		 int b = super.read();
		 if(b == -1 && this.signal != null)
			 this.signal();
		 long ms = System.currentTimeMillis();
		 while (b == -1)
		 {
			 b = super.read();
			if(this.timeout > 0 && b == -1 && (System.currentTimeMillis()-ms > this.timeout))
				return -1;//EOS due to DC
			 JREUtil.sleep(this.sleep);
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
		if(bytes_read < 1 && this.signal != null)
			this.signal();
		
		//if 0 bytes are read we have to wait till it reads at least 1 byte
		long ms = System.currentTimeMillis();
		while(bytes_read < 1)
		{
			bytes_read = read1(b, off, len);
			if(this.timeout > 0 && bytes_read < 1 && (System.currentTimeMillis()-ms > this.timeout))
				return -1;//EOS due to DC
			JREUtil.sleep(this.sleep);
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
	
	/**
	 * signal the IPC other process that you are waiting for input
	 */
	public void signal() throws IOException
	{
		if(this.out instanceof PrintStream)
			((PrintStream)this.out).print(this.signal);
		else
			this.out.write(this.signal.getBytes());
		this.out.flush();
	}
	
	public long getTimeOut()
	{
		return this.timeout;
	}
	
	public void setTimeOut(long time)
	{
		assert time != 0;
		this.timeout = time;
	}
}