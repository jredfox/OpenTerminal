package jml.ot.colors;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;

import jredfox.common.config.csv.CSV;
import jredfox.common.config.csv.CSVE;

public class Palette {
	
	public LinkedHashSet<Entry> entries = new LinkedHashSet<Entry>();
	public volatile boolean hasParsed;
	public String resource;
	
	public Palette()
	{
		
	}
	
	public Palette(String resource)
	{
		this.resource = resource;
	}
	
	/**
	 * return the Palette's closest color from RGB and then give you the Entry(code, name, rgb)
	 */
	public Entry pickColor(Color c)
	{
		this.check();
		int d = 0;
		Palette.Entry picked = null;
		for(Palette.Entry e : this.entries)
		{
			int r = e.rgb.getRed();
			int g = e.rgb.getGreen();
			int b = e.rgb.getBlue();
			int distence = Math.abs(r - c.getRed()) + Math.abs(g - c.getGreen()) + Math.abs(b - c.getBlue());
			if(picked == null || distence < d)
			{
				d = distence;
				picked = e;
			}
		}
		return picked;
	}

	public void parse(String strIn) throws IOException
	{
		this.parse(Palette.class.getClassLoader().getResourceAsStream(strIn));
	}
	
	public void parse(InputStream in) throws IOException
	{
		CSVE ce = new CSVE();
		ce.parse(in);
		for(CSV c : ce.list)
			this.add(Integer.parseInt(c.list.get(0)), c.list.get(1), new Color(Integer.parseInt(c.list.get(2)), Integer.parseInt(c.list.get(3)), Integer.parseInt(c.list.get(4))));
		this.hasParsed = true;
	}
	
	public void check() 
	{
		if(!this.hasParsed)
		{
			try
			{
				this.parse(this.resource);
			}
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
	}
	
	public void add(int code, String name, Color rgb)
	{
		this.entries.add(new Entry(code, name, rgb));
	}
	
	public void remove(int code)
	{
		this.entries.remove(new Entry(code, null, null));
	}
	
	public class Entry
	{
		public int code;
		public String name;
		public Color rgb;
		
		public Entry(int c, String n, Color color)
		{
			this.code = c;
			this.name = n;
			this.rgb = color;
		}
		
		@Override
		public int hashCode()
		{
			return this.code;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			Entry o = (Entry)obj;
			return this.code == o.code;
		}
		
		@Override
		public String toString()
		{
			return this.code + ", " + this.name + ", " + this.rgb;
		}
	}

}
