package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jml.ipc.pipes.Pipe.Type;
import jml.ot.OTConstants;
import jml.ot.colors.AnsiColors;
import jredfox.common.io.IOUtils;
import jredfox.common.utils.FileUtil;

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
	private File noREQFile = null;
	private BufferedReader noREQReader = null;
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
		
		File dirPipes = OTConstants.dirPipes;
		this.noREQFile = new File(dirPipes, "ot-NOREQ.txt");
		System.out.println(dirPipes + " SERVER?:" + (!OTConstants.LAUNCHED));
		
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
			IOUtils.saveFileLines(Arrays.asList("" + AnsiColors.TermColors.TRUE_COLOR), this.noREQFile , true);
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
			this.cleanup();
			server_out.replaceSYSO(true);
			server_out.replaceSYSO(false);
			client_in.replaceSYSO(false);
		}
	}

	/**
	 * cleans up files from previous launch
	 * @throws IOException 
	 */
	public void cleanup()
	{
		for(Pipe p : this.pipes.values())
		{
			if(p.type == Type.FILE)
			{
				p.file.delete();
				FileUtil.create(p.file);
			}
		}
		this.noREQFile.delete();
		FileUtil.create(this.noREQFile);
	}

	public void register(Pipe... ps)
	{
		for(Pipe p : ps)
			this.pipes.put(p.id, p);
	}

}
