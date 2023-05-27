package jml.ipc.pipes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * Wrapped PrintStream Object that will print both the old PrintStream and the new PrintStream at the same time.
 * @author jredfox
 */
public class WrappedPrintStream extends PrintStream {

	public PrintStream old;

	public WrappedPrintStream(PrintStream old, File file) throws FileNotFoundException {
		super(file);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, OutputStream out) {
		super(out);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, OutputStream out, boolean autoFlush) {
		super(out, autoFlush);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, OutputStream out, boolean autoFlush, String encoding)
			throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, String fileName) throws FileNotFoundException {
		super(fileName);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(fileName, csn);
		this.old = old;
	}

	public WrappedPrintStream(PrintStream old, File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(file, csn);
		this.old = old;
	}

	@Override
	public void flush() 
	{
		super.flush();
		this.old.flush();
	}

	@Override
	public void close() 
	{
		this.old.close();
		super.close();
	}

	@Override
	public boolean checkError() 
	{
		return this.old.checkError() || super.checkError();
	}
	
	@Override
	public void write(int b)
	{
		super.write(b);
		this.old.write(b);
	}
	
	@Override
	public void write(byte buf[], int off, int len) 
	{
		super.write(buf, off, len);
		this.old.write(buf, off, len);
	}
}
