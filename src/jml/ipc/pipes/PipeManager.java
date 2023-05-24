package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PipeManager {

	/**
	 * client's pipes
	 */
	public List<Pipe> cpipes = new ArrayList<>();
	public Pipe inputPipe;
	public Scanner scanner = new Scanner(System.in);
	/**
	 * Print this field from server to client to get input {@link Pipe#getServer()} from the client (CLI)
	 */
	public static final String INPUT_REQUEST = "/@<OT.IR>";

	public void start() throws IOException, InterruptedException
	{
		while(true)
		{
			this.init();
			this.tickClients();
		}
	}
	
	public void init() 
	{
		try 
		{
			this.inputPipe = new Pipe(new File("input.txt").getAbsoluteFile());
		} 
		catch (Throwable e) 
		{
			e.printStackTrace();
		}
	}

	//TODO: finish input
	public void tickClients() 
	{
		for (Pipe pipe : this.cpipes)
		{
			try
			{
			String q = "\"";
			BufferedReader r = pipe.getClient();
			PrintStream out = pipe.std ? System.out : System.err;
			String line = r.readLine();
			int i=0;
			while (line != null) 
			{
				if(line.contains(INPUT_REQUEST))
				{
					line = line.replace(INPUT_REQUEST, "");//remove the request from view
					if(!line.isEmpty())
						out.print("Line:" + (i++) + " " + q + line + q);
					String inLine = scanner.nextLine();
					this.inputPipe.getServer().write(inLine);
				}
				else
				{
					out.println("Line:" + (i++) + " " + q + line + q);
				}
				line = r.readLine();
			}
			}
			catch(Throwable t)
			{
				System.err.println("Error while ticking pipe:" + pipe.getId());
				t.printStackTrace();
			}
		}
	}

}
