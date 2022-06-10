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
	
	//XTERM COLORS STARTS HERE
	public static final TermColors BITS = TermColors.XTERM_16;
	public static final boolean hasRGB = BITS == TermColors.TRUE_COLOR;
	public static final Palette picker = new Palette();
	
	static
	{
		colors = colors == null ? "" : colors.replace("$", ";");
		parsePalette();
		enableCmdColors();
	}
	
	public static void enableCmdColors()
	{
		if(System.console() != null && TerminalUtil.isWindows())
		{
			String cls = " & cls";
			try
			{
				if(winTerm.equals("cmd"))
					new ProcessBuilder(new String[]{winTerm, "/c", "echo | set /p dummyName=" + getReset() + cls}).inheritIO().start().waitFor();
				else
					new ProcessBuilder(new String[]{winTerm, "/c", "Write-Host -NoNewLine " + getReset() + cls}).inheritIO().start().waitFor();
			}
			catch (Exception e){e.printStackTrace();}
			System.out.println(colors.replace(ESC, "ESC"));
		}
	}
	
	public static void parsePalette() 
	{
		try
		{
			picker.parse(AnsiColors.class.getClassLoader().getResourceAsStream(BITS == TermColors.XTERM_16 ? "resources/jml/ot/colors/xterm-16.csv" : "resources/jml/ot/colors/xterm-256.csv"));
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
	
	/**
	 * supports WIP: xterm-16, xterm-256 and true colors
	 */
	public static String formatColor(Color bg, Color text, String ansiEsq)
	{
		switch(BITS)
		{
			case TRUE_COLOR:
			{
				String b = bg == null ? "" : ESC + "[48;2;" + bg.getRed() + ";" + bg.getGreen() + ";" + bg.getBlue() + "m";
				String f = text == null ? "" : ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
				String a = ansiEsq == null ? "" : ansiEsq;
				return b + f + a;
			}
			case XTERM_256:
			{
				String b = bg == null ? "" : ESC + "[48;5;" + picker.pickColor(bg).code + "m";
				String f = text == null ? "" : ESC + "[38;5;" + picker.pickColor(text).code + "m";
				String a = ansiEsq == null ? "" : ansiEsq;
				return b + f + a;
			}
			case XTERM_16:
			{
				String b = bg == null ? "" : ESC + "[" + getANSI4BitColor((byte)picker.pickColor(bg).code, true) + "m";
				String f = text == null ? "" : ESC + "[" + getANSI4BitColor((byte)picker.pickColor(text).code, false) + "m";
				String a = ansiEsq == null ? "" : ansiEsq;
				return b + f + a;
			}
			default:
				return null;
		}
	}
	
	/**
	 * @param code 0-15
	 * @param background(boolean)
	 * @return the actual ANSI ESC code you need to use for that specific 4 bit color
	 */
	public static int getANSI4BitColor(byte code, boolean bg) 
	{
		int bgAdd = bg ? 10 : 0;//the variable to add to the some of the color code background is +10
		return code + (code < 8 ? 30 : 82) + bgAdd;//+30 as the offset ansi index. starting at the 8th code it has to switch to the next start of ANSI colors
	}

	/**
	 * format ANSI escape sequences that you don't see from above. this doesn't support escape codes with arguments such as colors
	 */
	public static String formatEsc(int code)
	{
		return ESC + "[" + code + "m";
	}
	
	public static enum TermColors
	{
		XTERM_16(),//16 different colors for lazy coders/ scripters not knowing RGB
		XTERM_256(),//one byte colors (0-255) legacy
		TRUE_COLOR()//RGB 24 bit standard colors with each color having 8 bits(0-255)
	}
}
