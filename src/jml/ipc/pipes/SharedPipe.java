package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class SharedPipe extends Pipe {
	
	public String id;
	
	public SharedPipe(String id)
	{
		this.id = id;
	}
	
	@Override
	public PrintWriter getServer()
	{
		return null;
	}
	
	@Override
	public BufferedReader getClient()
	{
		return null;
	}
}
