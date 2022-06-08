package jml.ot.colors;

import java.awt.Color;

public class AnsiColors {
	
	public static final String esq = "";
	private static final String RESET = esq + "[0m";
	public static final String BOLD = esq + "[1m";
	public static final String DIM = esq + "[2m";
	public static final String ITALIC = esq + "[3m";
	public static final String UNDERLINE = esq + "[4m";
	public static final String BLINK = esq + "[5m";
	public static final String BLINK_RAPID = esq + "[6m";
	public static final String INVERSE = esq + "[7m";
	public static final String HIDE = esq + "[8m";
	public static final String STRIKETHROUGH = esq + "[9m";
	public static final String UNDERLINE_DOUBLE = esq + "[21m";
	public static String colors = "";
	
	static
	{
		enableCmdColors();
	}
	
	public static void enableCmdColors()
	{
		try
		{
			new ProcessBuilder(new String[]{"cmd", "/c", "echo | set /p dummyName=[0m"}).inheritIO().start().waitFor();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * get the reset ansi esq for the whole program. doing esq[0m hard coded will cause the entire app to be reset back to default formating no additional styling
	 */
	public static String getReset()
	{
		return RESET + colors;
	}
	
	public static void setReset(Color background, Color text, boolean cls)
	{
		setReset(background, text, "", cls);
	}
	
	public static void setReset(Color background, Color text, String ansiEsq, boolean cls)
	{
		String bg = esq + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = esq + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		colors = bg + fg + ansiEsq;
		System.out.print(getReset());
		if(cls)
		{
			try
			{
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public static void print(Color background, Color text, String str)
	{
		String bg = background == null ? "" : esq + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = text == null ? "" : esq + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		System.out.print(bg + fg + str + getReset());
	}
	
	public static void println(Color background, Color text, String str)
	{
		String bg = background == null ? "" : esq + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = text == null ? "" : esq + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		System.out.println(bg + fg + str + getReset());
	}
}
