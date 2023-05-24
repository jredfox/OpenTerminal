package jml.ipc.pipes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import jml.ot.OTConstants;

public class PipeManager {
	
	public Map<String, Pipe> pipes = new HashMap<>();
	public Thread ticker;
	public volatile boolean isRunning;
	
	/**
	 * Print this field from server to client to get input {@link Pipe#getServer()} from the client (CLI)
	 */
	public static final String INPUT_REQUEST = "@#<IR>";
	
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
			p.tick();
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
				public void tick() 
				{
					try
					{
						if(this.getIn().ready())
						{
							String line = this.reader.readLine();
							while(line != null)
							{
								System.out.println(line);
								line = this.reader.readLine();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			};
			Pipe client_err = new PipeClient("ot.err", new File(dirPipes, "ot-err.txt"))
			{
				@Override
				public void tick() 
				{
					try
					{
						if(this.getIn().ready())
						{
							String line = this.reader.readLine();
							while(line != null)
							{
								System.err.println(line);
								line = this.reader.readLine();
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			};
			this.register(client_out, client_err);
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
			PipeServer server_err = new PipeServer("ot.err", new File(dirPipes, "ot-err.txt"))
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
			server_err.replaceSYSO(false);
			this.register(server_out, server_err, client_in);
		}
	}

	public void register(Pipe... ps) 
	{
		for(Pipe p : ps)
			this.pipes.put(p.id, p);
	}

}
