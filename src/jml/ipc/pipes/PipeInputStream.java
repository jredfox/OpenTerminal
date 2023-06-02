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
 * or Named) as it hangs when read() is called when there is no more data
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
	public long timeout = -1;
	/**
	 * send a msg if any to the outputstream provided that you are awaiting input from this stream
	 */
	public String signal;
	public PrintStream out;

	public PipeInputStream(File file, String signal, PrintStream out) throws FileNotFoundException
	{
		this(file, signal, out, 50);
	}

	public PipeInputStream(File file, String signal, PrintStream out, long sleep) throws FileNotFoundException 
	{
		super(file);
		this.signal = signal;
		this.out = out;
		this.sleep = sleep;
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
			 b = super.read();//give it one last chance to not eos before sleeping or timeout
			 if(b == -1)
			 {
				 if(this.shouldWake(false) || this.timeout > 0 && (System.currentTimeMillis()-ms > this.timeout))
					 return -1;//EOS due to DC
				 JREUtil.sleep(this.sleep);
				 if(this.shouldWake(true))
					 return -1;
			 }
		 }
		 return b;
	}
	
	/**
	 * When true the I/O will stop blocking and return EOS(-1). 
	 * It's fires directly before and after the sleep call.
	 * Override this if you need more then a timeout to determine when to stop BLOCKING I/O
	 * @param after is true after the sleep call
	 * @return true if it should stop BLOCKING I/O
	 */
	public boolean shouldWake(boolean after)
	{
		return false;
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
			bytes_read = read1(b, off, len);//give it one last chance to not eos before dc or sleeping
			if(bytes_read < 1)
			{
				if(this.shouldWake(false) || this.timeout > 0 && (System.currentTimeMillis()-ms > this.timeout))
					return -1;//EOS due to shouldWake or DC
				JREUtil.sleep(this.sleep);
				if(this.shouldWake(true))
					return -1;//EOS due to shouldWake
			}
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
	@SuppressWarnings("resource")
	public void signal() throws IOException
	{
		OutputStream o = this.out == null ? System.out : this.out;//dynamically grab's System.out in case it gets replaced in the future otherwise used fixed outputstream
		if(o instanceof PrintStream)
			((PrintStream)o).print(this.signal);
		o.flush();
	}
}
