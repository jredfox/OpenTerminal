package jml.ipc.pipes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import jml.ot.colors.AnsiColors;

public class ColoredPrintStream extends WrappedPrintStream {

	public String cs;// colored stream
	public AnsiColors c;// the colors impl

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, File file) throws FileNotFoundException {
		super(old, file);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, OutputStream out) {
		super(old, out);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, OutputStream out, boolean autoFlush) {
		super(old, out, autoFlush);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, OutputStream out, boolean autoFlush,
			String encoding) throws UnsupportedEncodingException {
		super(old, out, autoFlush, encoding);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, String fileName)
			throws FileNotFoundException {
		super(old, fileName);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, String fileName, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(old, fileName, csn);
		this.setColors(cs, col);
	}

	public ColoredPrintStream(String cs, AnsiColors col, PrintStream old, File file, String csn)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(old, file, csn);
		this.setColors(cs, col);
	}
	
//	@Override
//	public void print(boolean b)
//	{
//		this.print(b ? "true" : "false");
//	}
//	
//	@Override
//	public void print(boolean b)
//	{
//		
//	}

	/**
	 * set the colors
	 */
	public void setColors(String cs, AnsiColors c) {
		this.cs = cs;
		this.c = c;
	}

	public String prefix() {
		return this.cs != null ? this.cs : "";
	}

}
