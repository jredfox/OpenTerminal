package jml.ot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * .DAT file for OpenTerminal Session Transfer data
 * The id<byte>=value<String> with id incrementing every \n
 */
public class OTDat {
	
	public static final byte VERSION = 0;
	public static final byte MAXIDS = 20;
	
	public File f;
	public String[] arr;
	
	public OTDat(File f, byte v)
	{
		this.f = f;
		if(v != VERSION)
			throw new IllegalArgumentException("DAT VERSION MISMATCH");//sanity checker
	}

}
