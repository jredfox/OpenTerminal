package jml.ot;

import java.awt.Color;

import jml.ot.colors.Palette;

public class Debug {

	public static void main(String[] args)
	{
//		Color c = new Color(249,241,135);
//		System.out.println(c);
//		System.out.println(new Palette("resources/jml/ot/colors/ansi4bit-terminal.app.csv").pickColor(c));
	}
	
	public static Object getType(Object obj)
	{
		Object o = new Object();
		if(obj instanceof Byte)
			return o;
		else if(obj instanceof Short)
			return o;
		else if(obj instanceof Integer)
			return o;
		else if(obj instanceof Long)
			return o;
		else if(obj instanceof Float)
			return o;
		else if(obj instanceof Double)
			return o;
		else if(obj instanceof Boolean)
			return o;
		else if(obj instanceof String)
			return o;
		return null;
	}
	
}
