package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jredfox.common.file.FileUtils;
import jredfox.common.io.IOUtils;

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
	public boolean useWrappedIn;
	public File noREQFile = null;
	private BufferedReader noREQReader = null;
	public boolean isClient;
	public File dirPipes;
	public static final String REQUEST_INPUT = "@<OT.IN>";
	
	public PipeManager()
	{
		
	}
	
	public void register(Pipe p)
	{
		this.pipes.put(p.id, p);
	}
	
	/**
	 * assumes the client has already sent the input
	 */
	public String getInputNoREQ() throws IOException
	{
		if(this.noREQReader == null)
			this.noREQReader = IOUtils.getReader(this.noREQFile);
		String input = this.noREQReader.readLine();
		while(input == null)
			input = this.noREQReader.readLine();
		return input;
	}
	
	public PrintStream noReQPrinter;
	public void printNoREQ(String s)
	{
		if(!this.isClient)
			throw new IllegalArgumentException("");
		if(this.noReQPrinter == null)
		{
			try {
				this.noReQPrinter = new PrintStream(new FileOutputStream(this.noREQFile), true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.noReQPrinter.println(s);
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
				System.exit(-1);//Crash the App an error occurred while ticking
			}
			//TODO: check PID's is alive here based on if is host or CLI and then stop this thread
		}
		this.isTicking = false;
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
	
	public void loadPipes(File dirPipes, boolean client, boolean replaceSYSO)
	{
		this.dirPipes = dirPipes;
		this.isClient = client;
		this.noREQFile = new File(dirPipes, "ot-NOREQ.txt");
		System.out.println(dirPipes + " isHost:" + (!this.isClient));
		
		//client side
		if(client)
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
								System.out.flush();//ensure it flushes to display before gathering the input
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
								System.out.write(m.getBytes());//write the characters to the output that we confirmed the ignore string wasn't there
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
		}
		//server side
		else
		{
			this.cleanup();
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
				
				@Override
				public InputStream getIn()
				{
					try
					{
						if(this.in == null)
							this.in = new PipeInputStream(this.file);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return this.in;
				}
			};
			
			this.register(server_out, client_in);
			if(replaceSYSO)
			{
				server_out.replaceSYSO(true);
				server_out.replaceSYSO(false);
				client_in.replaceSYSO(this.useWrappedIn);
			}
		}
	}

	/**
	 * cleans up files from previous launches
	 * @throws IOException 
	 */
	public void cleanup()
	{
		IOUtils.deleteDirectory(this.dirPipes.getParentFile());
		this.dirPipes.mkdirs();
		FileUtils.create(this.noREQFile);
	}

	public void register(Pipe... ps)
	{
		for(Pipe p : ps)
			this.pipes.put(p.id, p);
	}

}
