package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jml.ot.OTConstants;
import jml.ot.colors.AnsiColors;
import jredfox.common.io.IOUtils;

/**
 * A standard PipeManager used to configure STD, ERR, and IN from server to client. The STD & ERR will only display the output and the IN will only gather input.
 * if you need more then this simply override the load pipes method
 * @author jredfox
 *
 */
public class PipeManager {
	
	public Map<String, Pipe> pipes = new HashMap<>();
	public Thread ticker;
	public volatile boolean isRunning;
	
	public PipeManager()
	{
		
	}
	
	public void register(Pipe p)
	{
		this.pipes.put(p.id, p);
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
					PipeManager.this.tick();
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
			Pipe client_out = new PipeClient("ot.out", new File(dirPipes, "ot-out.txt"))
			{
				@Override
				public void tick() throws IOException 
				{
					if(this.getReader().ready())
					{
						String line = this.reader.readLine();
						while(line != null)
						{
							System.out.println(line);
							line = this.reader.readLine();
						}
					}
				}
			};
			
			//write all data from System#in to the the server
			PipeServer server_in = new PipeServer("ot.in", new File(dirPipes, "ot-in.txt"))
			{
				public BufferedReader reader;
				
				@Override
				public void tick() throws IOException 
				{
					//make sure that the reader won't block before writing the input to the server's inputstream
					if(this.getReader().ready())
					{
						String line = this.reader.readLine();
						while(line != null)
						{
							this.out.println(line);
							line = this.reader.readLine();
						}
					}
				}

				public BufferedReader getReader()
				{
					if(this.reader == null)
						this.reader = IOUtils.getReader(System.in);
					return this.reader;
				}
			};
			this.register(client_out, server_in);
			server_in.getOut().println(AnsiColors.TermColors.TRUE_COLOR);//TODO: FIX IN FUTURE System.getProperty("ot.color.mode")
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
