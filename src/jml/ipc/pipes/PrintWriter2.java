package jml.ipc.pipes;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * PrintWriter with autoflush fixed on {@link PrintWriter#print(String)}
 * @author jredfox
 */
public class PrintWriter2 extends PrintWriter{
	public boolean autoFlush;
	
	public PrintWriter2 (Writer out, boolean auto)
	{
		super(out,auto);
		this.autoFlush = auto;
	}
	
    /**
     * Writes a portion of a string.
     * @param s A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
	@Override
    public void write(String s, int off, int len)
    {
        try {
            synchronized (lock) {
                ensureOpen();
                out.write(s, off, len);
                if (autoFlush) //&& (s.indexOf('\n') >= 0))
                    out.flush();
            }
        }
        catch (InterruptedIOException x) {
            Thread.currentThread().interrupt();
        }
        catch (IOException x) {
        	this.setError();
        }
    }
    
    protected void ensureOpen() throws IOException {
        if (this.out == null)
            throw new IOException("Stream closed");
    }

}
