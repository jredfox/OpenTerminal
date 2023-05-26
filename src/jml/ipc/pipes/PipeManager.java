package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jml.ot.OTConstants;
import jml.ot.colors.AnsiColors;

/**
 * A standard PipeManager used to configure STD, ERR, and IN from server to client. The STD & ERR will only display the output and the IN will only gather input.
 * if you need more then this simply override the loadPipes() method
 * @author jredfox
 */
public class PipeManager {
	
	public Map<String, Pipe> pipes = new HashMap<>();
	public Thread ticker;
	public volatile boolean isRunning;
	public volatile boolean isTicking = true;
	/**
	 * IPC FILE ONLY Max Timeout before the input method returns null from timeout
	 */
	public long fileInTimeout = -1;
	/**
	 * IPC FILE ONLY How Long to Sleep before the input method trys to read again
	 */
	public long fileInSleep = 100;
	public static final String REQUEST_INPUT = "@<OT.IN>";
	
	public PipeManager()
	{
		
	}
	
	public void register(Pipe p)
	{
		this.pipes.put(p.id, p);
	}
	
	/**
	 * IPC FILE ONLY get input from the client.
	 */
	public String getInput() throws IOException
	{
		return this.getInput(System.out);
	}
	
	/**
	 * IPC FILE ONLY get input from the client. 
	 * @param PrintStream being the underlying printstream from the Pipe. The default method assumes you replaced {@link System#out} and are using it for the intended CLI
	 */
	public String getInput(PrintStream out) throws IOException
	{
		PipeClient pipeIn = (PipeClient) this.pipes.get("ot.in");
		BufferedReader reader = pipeIn.getReader();
		String input = reader.readLine();
		if(input != null)
			return input;
		
		out.print(REQUEST_INPUT);
		out.flush();
		long ms = System.currentTimeMillis();
		while(input == null)
		{
			input = reader.readLine();
		}
		return input;
	}
	
	/**
	 * assumes the client has already sent the input
	 */
	public String getInputNoREQ() throws IOException
	{
		PipeClient pipeIn = (PipeClient) this.pipes.get("ot.in");
		BufferedReader reader = pipeIn.getReader();
		String input = reader.readLine();
		while(input == null)
			input = reader.readLine();
		return input;
	}
	
	public void tick()
	{
		for(Pipe p : this.pipes.values())
		{
			try
			{
				p.tick();
			}
			catch(Throwable t)
			{
				System.err.println("error while ticking pipe:" + p.id + ("\nisHost:" + OTConstants.LAUNCHED) + "\nisPipeServer:" + (p instanceof PipeServer));
				t.printStackTrace();
			}
			//TODO: check PID's is alive here based on if is host or CLI and then stop this thread
		}
	}
	
	public void start()
	{
		this.ticker = new Thread()
		{
			@Override
			public void start()
			{
				PipeManager.this.isRunning = true;
				super.start();
			}
			
			@Override
			public void run()
			{
				do
				{
					PipeManager.this.isTicking = true;
					PipeManager.this.tick();
					PipeManager.this.isTicking = false;
				}
				while(PipeManager.this.isRunning);
			}
		};
		this.ticker.start();
	}
	
	public void loadPipes()
	{
		//TODO:on client side make this sync instead of generate new pipes
		//create dir pipes session and make sure it's one that doesn't exist yet
//		File dirPipes = new File(OTConstants.home, "pipes/" + UUID.randomUUID());
//		while(dirPipes.exists())
//			dirPipes = new File(OTConstants.home, "pipes/" + UUID.randomUUID());
		
		File dirPipes = new File(OTConstants.home, "pipes");
		
		//client side
		if(OTConstants.LAUNCHED)
		{
			//write all data from System#in to the the server
			PipeServer server_in = new PipeServer("ot.in", new File(dirPipes, "ot-in.txt"))
			{	
				@Override
				public void tick() throws IOException 
				{
					
				}
			};
			Pipe client_out = new PipeClient("ot.out", new File(dirPipes, "ot-out.txt"))
			{
				@Override
				public void tick() throws IOException 
				{
					int c = 0;//flush counter
					
					//ignore flags
					String m = "";
					int sindex = 0;
					
					int b = this.getIn().read();
					while(b != -1)
					{
						if(PipeManager.REQUEST_INPUT.charAt(sindex) == (char)b)
						{
							m += (char)b;
							sindex++;
							if(m.equals(PipeManager.REQUEST_INPUT))
							{
								Scanner scanner = new Scanner(System.in);
								String input = scanner.nextLine();
								server_in.getOut().println(input);
							}
							b = in.read();//ensure read increments
							continue;
						}
						else
						{
							if(!m.isEmpty())
							{
								System.out.write(m.getBytes());//write the characters to the output that we confirmed the ignore string wasn't there
							}
							sindex = 0;//reset data
							m = "";
						}
						
						//do the actual copying from in to out
						System.out.write(b);
						b = in.read();
						
						//auto flush every 4,000 bytes
						c++;
						if(c >= 4000)
						{
							System.out.flush();
							c = 0;
						}
					}
					//ensure it flushes even without any newline and it's a printstream
					System.out.flush();
				}
			};
			this.register(client_out, server_in);
			server_in.getOut().println(AnsiColors.TermColors.TRUE_COLOR);
		}
		//server side
		else
		{
			PipeServer server_out = new PipeServer("ot.out", new File(dirPipes, "ot-out.txt"))
			{
				@Override
				public void tick() 
				{
					
				}
			};
			//fetch input from the CLI
			PipeClient client_in = new PipeClient("ot.in", new File(dirPipes, "ot-in.txt"))
			{
				@Override
				public void tick()
				{
					
				}
			};
			server_out.replaceSYSO(true);
			server_out.replaceSYSO(false);
			client_in.replaceSYSO(false);
			this.register(server_out, client_in);
		}
	}

	public void register(Pipe... ps)
	{
		for(Pipe p : ps)
			this.pipes.put(p.id, p);
	}

}
