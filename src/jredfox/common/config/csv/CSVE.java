package jredfox.common.config.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jredfox.common.io.IOUtils;
import jredfox.common.utils.JavaUtil;

/**
 * dog**** API do not use it was created when I first learned java.
 * @author jredfox
 */
public class CSVE {
	
	public ArrayList<CSV> vars = new ArrayList<CSV>();
	public ArrayList<CSV> list = new ArrayList<CSV>();
	
	public CSVE(){}//default object with nothing in it
	
	public CSVE(File f)
	{
		try {
			this.parse(new FileInputStream(f));
		} catch (Exception e) {e.printStackTrace();}
	}
	/**
	 * returns the csv line
	 */
	public CSV getCSV(String var)
	{
		for(CSV csv : vars)
			if(csv.list.get(0).equals(var))
				return csv;
		for(CSV csv : list)
			if(csv.list.get(0).equals(var))
				return csv;
		return null;
	}
	/**
	 * Returns the value of the variable from the csv 
	 */
	public String getValue(CSV csv,int placement)
	{
		String var = csv.list.get(placement);//placement on the line
		if(!var.contains("\""))
			return var;
		for(CSV c : this.vars)
		{
			String[] parts = c.list.get(0).split(":");
			String name = parts[0];
			int index = 1;
			if(parts.length > 1)
			{
				parts[1] = JavaUtil.toWhiteSpaced(parts[1]);
				index = Integer.parseInt(parts[1]);
			}
			if(name.equals(var))
				return c.list.get(index);
		}
		return null;
	}
	/**
	 * returns the variable based on the value
	 */
	public String getVariable(String path,int index)
	{
		return getValue(getCSV(path),index);
	}
	
	public void parse(InputStream in) throws Exception
	{
		List<String> init = IOUtils.getFileLines(new BufferedReader(new InputStreamReader(in)));
		boolean header = false;
		boolean body = false;
		for(String s : init)
		{
			String str = JavaUtil.toWhiteSpaced(s);
			if(str.equals("") || str.indexOf("#") == 0)
				continue;
			if(str.equals("<Header>"))
			{
				header = true;
				continue;
			}
			if(str.equals("</Header>") )
			{
				header = false;
				body = true;
				continue;
			}
			if(header)
				this.vars.add(new CSV(str));
			else
				this.list.add(new CSV(str));
		}
	}
	@Override
	public String toString(){return "Vars:" + this.vars + "\nList:" +  this.list;}

}