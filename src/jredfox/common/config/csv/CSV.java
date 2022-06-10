package jredfox.common.config.csv;

import java.util.ArrayList;
import java.util.List;

import jredfox.common.utils.JavaUtil;

public class CSV {
	public List<String> list = new ArrayList<String>();
	public boolean tst;
	
	public CSV(String s)
	{
		String[] parts = JavaUtil.toWhiteSpaced(s).split(",");
		for(String ss : parts)
		{
			list.add(ss.replace('\u00A0',' ').trim());
		}
	}
	
	public String toString()
	{
		StringBuilder b = new StringBuilder();
		for(String s : list)
			b.append(s + ",");
		return b.toString().substring(0, b.length() -2);
	}

}