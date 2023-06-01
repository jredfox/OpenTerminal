package jml.ipc.pipes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class FilePipeManager extends PipeManager {
	
	public File dirPipes, log;
	
	public FilePipeManager(boolean client, boolean replaceSYSO, File dirPipes, File log)
	{
		super(client, replaceSYSO);
		this.dirPipes = dirPipes;
		this.log = log;
	}

	@Override
	public void loadPipes()
	{
		File std = this.log != null ? this.log : new File(this.dirPipes, "ot-out.txt");
		
		//client side
		if(this.isClient)
		{
			//write all data from System#in to the the server
			PipeServer server_in = new PipeServer("ot.in", new File(this.dirPipes, "ot-in.txt"))
			{
				@Override
				public void tick() throws IOException 
				{
					
				}
			};
			Pipe client_out = new PipeClient("ot.out", std)
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
								sindex = 0;//reset data
								m = "";
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
			PipeServer server_out = new PipeServer("ot.out", std)
			{
				@Override
				public void tick() 
				{
					
				}
			};
			//fetch input from the CLI
			PipeClient client_in = new PipeClient("ot.in", new File(this.dirPipes, "ot-in.txt"))
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
							this.in = new PipeInputStream(this.file, PipeManager.REQUEST_INPUT, FilePipeManager.this.replaceSYSO ? null : server_out.getOut());
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					return this.in;
				}
			};
			this.register(server_out, client_in);
			
			if(this.replaceSYSO)
			{
				//don't replace SYSO if we are logging with IPC TYPE file to prevent too much I/O on the disk
				if(this.log == null)
				{
					server_out.replaceSYSO(true);//replace syso both streams to the same file to prevent de-sync
					server_out.replaceSYSO(false);
				}
				client_in.replaceSYSO(false);
			}
		}
	}

}
