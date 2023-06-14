package jml.ipc.pipes;

import java.util.HashMap;
import java.util.Map;

/**
 * A standard PipeManager used to configure STD, ERR, and IN from server to client. The STD & ERR will only display the output and the IN will only gather input.
 * if you need more then this simply override the loadPipes() method
 * @author jredfox
 */
public abstract class PipeManager {
	
	//final vars
	public static final String REQUEST_INPUT = "@<OT.IN>";
	public static final int REQ_LEN = REQUEST_INPUT.length();
	
	public Map<String, Pipe> pipes = new HashMap<>();
	public boolean isClient;
	public boolean replaceSYSO = true;
	
	//thread vars
	public volatile boolean isRunning;
	public volatile boolean isTicking = true;
	public boolean hasErr;
	public Thread ticker;
	
	public PipeManager()
	{
		
	}
	
	public PipeManager(boolean client, boolean replaceSYSO)
	{
		this.isClient = client;
		this.replaceSYSO = replaceSYSO;
	}
	
	public void register(Pipe... ps)
	{
		for(Pipe p : ps)
			this.pipes.put(p.id, p);
	}
	
	public <T extends Pipe> Pipe get(String id)
	{
		return this.pipes.get(id);
	}
	
	public void start()
	{
		this.ticker = new Thread()
		{
			@Override
			public void start()
			{
				PipeManager.this.isRunning = true;
				PipeManager.this.loadPipes();
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
		this.ticker.setDaemon(!this.isClient);//prevent this from stopping the server from shutting down
		this.ticker.start();
	}
	
	public void tick()
	{
		this.isTicking = true;
		for(Pipe p : this.pipes.values())
		{
			try
			{
				p.tick();
			}
			catch(Throwable t)
			{
				System.err.println("Error while ticking pipe:" + p.id + ("\nisHost:" + !this.isClient) + "\nisPipeServer:" + (p instanceof PipeServer));
				t.printStackTrace();
				this.isRunning = false;
				this.hasErr = true;
			}
			//TODO: check PID's is alive here based on if is host or CLI and then stop this thread
		}
		this.isTicking = false;
	}
	
	/**
	 * Load all your IPC Pipes here
	 */
	public abstract void loadPipes();

}
