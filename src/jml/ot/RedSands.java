package jml.ot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RedSands {
	
	public static void main(String[] args) throws IOException
	{
		Set<String> list = new TreeSet<>(new Comparator<String>()
				{
					@Override
					public int compare(String t1, String t2) 
					{
						return t1.toLowerCase().compareTo(t2.toLowerCase());
					}
			
				}
			);
		for(String s : OSUtil.linux_terminals)
			list.add(s);
		for(String s : list)
			System.out.println("\"" + s + "\",");
//		System.out.println("starting redsands...");
//		while(true)
//		{
//			try 
//			{
//				Thread.sleep(1000);
//			} 
//			catch (InterruptedException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}

}
