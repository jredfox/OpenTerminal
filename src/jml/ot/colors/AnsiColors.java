package jml.ot.colors;

import java.awt.Color;

import jml.ot.TerminalUtil;

public class AnsiColors {
	
	public static final String ESC = "\033";
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
	/**
	 * not supported in windows
	 */
	public static final String FRAMED = ESC + "[51m";
	/**
	 * not supported in windows
	 */
	public static final String ENCIRCLED = ESC + "[52m";
	public static final String OVERLINE = ESC + "[53m";
	public static String colors = System.getProperty("ot.ansi.colors");
	private static String winTerm = TerminalUtil.isExeValid("cmd") ? "cmd" : "powershell";
	
	static
	{
		colors = colors == null ? "" : colors.replace("$", ";");
		enableCmdColors();
	}
	
	public static void enableCmdColors()
	{
		if(System.console() != null && TerminalUtil.isWindows())
		{
			try
			{
				if(winTerm.equals("cmd"))
					new ProcessBuilder(new String[]{winTerm, "/c", "echo | set /p dummyName=" + getReset() + " & cls"}).inheritIO().start().waitFor();
				else
					new ProcessBuilder(new String[]{winTerm, "/c", "Write-Host -NoNewLine " + getReset() + " & cls"}).inheritIO().start().waitFor();
			}
			catch (Exception e){e.printStackTrace();}
		}
	}
	
	/**
	 * get the reset ansi esq for the whole program. doing esq[0m hard coded will cause the entire app to be reset back to default formating no additional styling
	 */
	public static String getReset()
	{
		return RESET + colors;
	}
	
	/**
	 * this will return the default ansi escape reset sequence without the colors this will override the entire console app's settings if used
	 */
	public static String getNonColoredReset()
	{
		return RESET;
	}
	
	public static void setReset(Color background, Color text, boolean cls)
	{
		setReset(background, text, "", cls);
	}
	
	public static void setReset(Color background, Color text, String ansiEsc, boolean cls)
	{
		colors = formatColor(background, text, ansiEsc);
		System.out.print(getReset());
		if(cls && TerminalUtil.isWindows())
		{
			try
			{
				new ProcessBuilder(winTerm, "/c", "cls").inheritIO().start().waitFor();
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public static void print(Color background, Color text, String str)
	{
		System.out.print(formatColor(background, text, "") + str + getReset());
	}
	
	public static void println(Color background, Color text, String str)
	{
		System.out.println(formatColor(background, text, str) + getReset());
	}
	
	public static String formatColor(Color bg, Color text, String ansiEsq)
	{
		String b = bg == null ? "" : ESC + "[48;2;" + bg.getRed() + ";" + bg.getGreen() + ";" + bg.getBlue() + "m";
		String f = text == null ? "" : ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
		String a = ansiEsq == null ? "" : ansiEsq;
		return b + f + a;
	}
	
	/**
	 * format ANSI escape sequences that you don't see from above. this doesn't support escape codes with arguments such as colors
	 */
	public static String formatEsc(int code)
	{
		return ESC + "[" + code + "m";
	}
}
