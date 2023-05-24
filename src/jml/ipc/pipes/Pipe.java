package jml.ipc.pipes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/**
 * pipes are a one way outputstream from the server to the client(s)
 * (inputstreams) that they then read. jml.ipc.pipes only currently supports
 * File, URL, NamedPipes. Named Pipes are the fastest as it only uses read/write
 * to RAM.
 * 
 * @author jredfox
 */
public class Pipe implements Closeable {

	public String id;
	public PrintWriter out;
	public BufferedReader in;
	public InputStream i;
	public URL path;
	public boolean std;// STD for true ERR for false
	public boolean needsInput;

	public Pipe(File f) throws IOException {
		this(f, true);
	}

	public Pipe(File f, boolean std) throws IOException {
		this(f.toURI().toURL());
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
		this.std = std;
	}

	public Pipe(URL u) {
		this.path = u;
	}

	protected Pipe() {

	}
	
	public String getId()
	{
		return this.id != null ? this.id : this.path.toString();
	}

	/**
	 * create the Pipe server
	 */
	public PrintWriter getServer() {
		if (this.out == null) {
			try {
				this.path.getProtocol();
				if (this.path.getProtocol().equalsIgnoreCase("file")) {
					File f = new File(this.path.toURI());
					this.out = this.getPrintWriter(f);
				} else {
					URLConnection con = this.path.openConnection();
					con.setDoOutput(true);
					con.setConnectTimeout(10000);
					con.connect();
					this.out = this.getPrintWriter(con.getOutputStream());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.out;
	}

	protected PrintWriter getPrintWriter(File f) throws IOException {
		return new PrintWriter2(new BufferedWriter(new FileWriter(f)), true);
	}

	protected PrintWriter getPrintWriter(OutputStream out) throws FileNotFoundException {
		return new PrintWriter2(new BufferedWriter(new OutputStreamWriter(out)), true);
	}

	/**
	 * create the Pipe client
	 */
	public BufferedReader getClient() 
	{
		if (this.in == null)
		{
			try 
			{
				this.i = this.path.openConnection().getInputStream();
				this.in = new BufferedReader(new InputStreamReader(this.i, StandardCharsets.UTF_8));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return this.in;
	}

	@Override
	public void close() {
		if (this.out != null) {
			try {
				this.out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (this.in != null) {
			try {
				this.in.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean needsInput()
	{
		return this.needsInput;
	}
	
	public void setNeedsInput(boolean b)
	{
		this.needsInput = b;
	}

}
