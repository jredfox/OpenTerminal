package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import jredfox.common.file.FileUtils;
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
	
	public BufferedReader getReader() throws IOException
	{
		if(this.in == null)
		{
			FileUtils.create(this.file);//ensure it exists on client as server may lag
			this.in = this.url.openConnection().getInputStream();
			this.reader = IOUtils.getReader(this.in);
		}
		return this.reader;
	}
	
	@Override
	public void replaceSYSO(boolean std)
	{
		//TODO: create a wrapper to not conflict
		System.setIn(this.in);
	}

	@Override
	public void close() throws IOException
	{
		this.in.close();
	}

}
