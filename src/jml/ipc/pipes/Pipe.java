package jml.ipc.pipes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import jredfox.common.file.FileUtils;

public abstract class Pipe {
	
	public String id;
	public URL url;
	public Type type;
	/**
	 * will be null unless the type is file
	 */
	public File file;
	
	public Pipe(String id)
	{
		this.id = id;
	}
	
	public Pipe(String id, File f)
	{
		this(id, FileUtils.toURL(f));
		this.file = f;
		this.type = Pipe.Type.FILE;
	}
	
	public Pipe(String id, URL u)
	{
		this(id);
		this.url = u;
		this.type = Pipe.Type.URL;
	}
	
	/**
	 * replaces the STD, ERR or IN. this is so the programmer doesn't have to manually call {@link PipeServer#getOut()} or {@link PipeClient#getReader() every time they want to print or read something}
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	public abstract void replaceSYSO(boolean std) throws URISyntaxException, IOException;
	
	/**
	 * user must impl this for every different use
	 */
	public abstract void tick() throws Throwable;
	
	public static enum Type
	{
		FILE,
		URL,
		SOCKET,//socket pipes or LAN/NET pipes can be viewed across the network or NET if your port is forwarded
		SHARED//shared pipes are similar to named pipes but only accessible to the same machine on your ram
	}

}
