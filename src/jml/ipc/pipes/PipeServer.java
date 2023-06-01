package jml.ipc.pipes;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import jredfox.common.io.IOUtils;

public abstract class PipeServer extends Pipe implements Closeable {
	
	public PrintStream out;
	
	public PipeServer(String id)
	{
		super(id);
	}
	
	public PipeServer(String id, File f)
	{
		super(id, f);
	}
	
	public PipeServer(String id, URL u)
	{
		super(id, u);
	}
	
	public PrintStream getOut()
	{
		try{
		if(this.out == null)
		{
			switch(this.type)
			{
				case FILE:
				{
					this.out = new PrintStream(new FileOutputStream(this.file), true);//https://bugs.openjdk.org/browse/JDK-4814217
					break;
				}
				case URL:
				{
					URLConnection con = this.url.openConnection();
					con.setConnectTimeout(7500);
					con.setDoOutput(true);
					con.connect();
					this.out = new PrintStream(con.getOutputStream(), true);
					break;
				}
				default:
					break;
			}
		}
		}
		catch(Throwable t){
			t.printStackTrace();
		}
		return this.out;
	}
	
	/**
	 * replace the System.out / Sytem.err to ensure user friendliness
	 * @throws IOException 
	 * @throws URISyntaxException 
	 */
	@Override
	public void replaceSYSO(boolean std)
	{
		PrintStream out = new WrappedPrintStream((std ? System.out : System.err), this.getOut());
		if(std)
			System.setOut(out);
		else
			System.setErr(out);
	}

	/**
	 * user must implement this on their own behalf
	 */
	@Override
	public abstract void tick() throws Throwable;
	
	@Override
	public void close()
	{
		IOUtils.closeQuietly(this.out);
	}

}
