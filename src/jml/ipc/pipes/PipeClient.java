package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jredfox.common.io.IOUtils;

public abstract class PipeClient extends Pipe implements Closeable {

	public BufferedReader reader;
	public InputStream in;
	
	public PipeClient(String id)
	{
		super(id);
	}
	
	public PipeClient(String id, File f)
	{
		super(id, f);
	}
	
	public PipeClient(String id, URL u)
	{
		super(id, u);
	}
	
	public BufferedReader getReader()
	{
		if(this.reader == null)
			this.reader = IOUtils.getReader(this.createIn());
		return this.reader;
	}
	
	public InputStream getIn()
	{
		if(this.in == null)
			this.in = this.createIn();
		return this.in;
	}
	
	public InputStream createIn()
	{
		try 
		{
			if(this.type == Type.FILE)
				return new FileInputStream(this.file);
			return this.url.openConnection().getInputStream();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void replaceSYSO(boolean wrapper)
	{
		System.setIn(this.getIn());
	}

	/**
	 * WARNING: could close system.in if you called replacedSyso
	 */
	@Override
	public void close() throws IOException
	{
		IOUtils.closeQuietly(this.in);
		IOUtils.closeQuietly(this.reader);
	}

}
