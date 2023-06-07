package jml.ot;

import java.io.PrintStream;

import jredfox.common.io.NullOutputStream;

public class ControlStream extends PrintStream {
	
	public PrintStream old;
	public volatile boolean enabled = true;
	
	public ControlStream(PrintStream old)
	{
		super(new NullOutputStream());
		this.old = old;
	}
	
	public void setEnabled(boolean b)
	{
		this.enabled = b;
	}
	
	@Override
	public void flush() 
	{
		if(this.enabled)
			this.old.flush();
	}

	@Override
	public void close() 
	{
		if(this.enabled)
			this.old.close();
	}

	@Override
	public boolean checkError() 
	{
		return this.old.checkError();
	}
	
	@Override
	public void write(int b)
	{
		if(this.enabled)
			this.old.write(b);
	}
	
	@Override
	public void write(byte buf[], int off, int len) 
	{
		if(this.enabled)
			this.old.write(buf, off, len);
	}
	
	@Override
	public PrintStream append(CharSequence csq, int start, int end) 
	{
		if(!this.enabled)
			return this;
        CharSequence cs = (csq == null ? "null" : csq);
        this.print(cs.subSequence(start, end).toString());
        return this;
	}

}
