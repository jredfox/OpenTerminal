package jml.ot.colors;

import java.awt.Color;

public class AnsiColors {
	
	public static final String ESC = "";
	private static final String RESET = ESC + "[0m";
	public static final String BOLD = ESC + "[1m";
	public static final String DIM = ESC + "[2m";
	public static final String ITALIC = ESC + "[3m";
	public static final String UNDERLINE = ESC + "[4m";
	public static final String BLINK = ESC + "[5m";
	public static final String BLINK_RAPID = ESC + "[6m";
	public static final String INVERSE = ESC + "[7m";
	public static final String HIDE = ESC + "[8m";
	public static final String STRIKETHROUGH = ESC + "[9m";
	public static final String UNDERLINE_DOUBLE = ESC + "[21m";
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
		String bg = ESC + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
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
		String bg = background == null ? "" : ESC + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = text == null ? "" : ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		System.out.print(bg + fg + str + getReset());
	}
	
	public static void println(Color background, Color text, String str)
	{
		String bg = background == null ? "" : ESC + "[48;2;" + background.getRed() + ";" + background.getGreen() + ";" + background.getBlue() + "m";
		String fg = text == null ? "" : ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		System.out.println(bg + fg + str + getReset());
	}
}
