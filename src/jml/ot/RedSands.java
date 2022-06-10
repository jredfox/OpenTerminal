package jml.ot;

import java.awt.Color;
import java.io.File;

import jml.ot.colors.AnsiColors;

public class RedSands {
	
	public static void main(String[] args) throws Exception
	{
//		Palette p = new Palette();
//		p.parse(RedSands.class.getClassLoader().getResourceAsStream("resources/jml/ot/colors/xterm-256.csv"));
//		Color c = new Color(80, 255, 255);
//		Color d = Color.YELLOW;
		Color c = Color.GREEN;
		System.out.println(c);
		System.out.println(AnsiColors.picker.pickColor(c));
//		System.out.println(AnsiColors.to8Bit(new Color(255, 255, 255)));
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
	
	public static String findExe(String name)
	{
	    for (String dirname : System.getenv("PATH").split(File.pathSeparator)) 
	    {
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute())
	        {
	            return file.getAbsolutePath();
	        }
	    }
	    return null;
	}
	

}
