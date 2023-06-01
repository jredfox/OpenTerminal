package jml.ot.colors;

import java.awt.Color;
import java.io.PrintStream;

import jml.ipc.pipes.WrappedPrintStream;
import jredfox.common.io.NullOutputStream;

/**
 * A Wrapped PrintStream that supports prefixing and suffixing colors
 * @author jredfox
 */
public class ColoredPrintStream extends WrappedPrintStream {

	public String cs;// colored stream
	public AnsiColors c;// the colors impl
	/**
	 * when true this will make the prefix reset 0m instead of 0m + STD's COLOR
	 */
	public boolean override;
	
	/**
	 * bg, fg or ansi can be null safley
	 */
	public ColoredPrintStream(Color bg, Color fg, String ansiEsc, AnsiColors c, PrintStream old)
	{
		this(bg, fg, ansiEsc, c, false, old);
	}
	
	/**
	 * bg, fg or ansi can be null safley
	 */
	public ColoredPrintStream(Color bg, Color fg, String ansiEsc, AnsiColors c, boolean override, PrintStream old)
	{
		this(c.formatColor(bg, fg, ansiEsc, false), c, old);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old) 
	{
		super(old, new NullOutputStream());
		this.setColors(cs, col);
	}
	
	@Override
	public void print(String s)
	{
		super.print(this.prefix() + s + this.suffix());
	}
	
	@Override
	public void print(boolean b) 
	{
		this.print(String.valueOf(b));
	}
	
	@Override
	public void print(int i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public void print(long i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public void print(float i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public void print(char c) 
	{
		this.print(String.valueOf(c));
	}
	
	@Override
	public void print(double i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public void print(char[] i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public void print(Object i) 
	{
		this.print(String.valueOf(i));
	}
	
	@Override
	public PrintStream append(CharSequence csq, int start, int end) 
	{
        CharSequence cs = (csq == null ? "null" : csq);
        this.print(cs.subSequence(start, end).toString());
        return this;
	}
	
	public String prefix()
	{
		return (this.override ? this.c.getNonColoredReset() : this.c.getReset()) + this.cs;
	}

	/**
	 * @return STD color from {@link AnsiColors#getReset()}
	 */
	public String suffix() 
	{
		return this.c.getReset();
	}
	
	/**
	 * set the colors
	 */
	public void setColors(String cs, AnsiColors c)
	{
		this.cs = cs;
		this.c = c;
	}

}
