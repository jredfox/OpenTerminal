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
	private static final String winTerm = TerminalUtil.isWindows() ? (TerminalUtil.isExeValid("cmd") ? "cmd" : "powershell") : "";
	
	/**
	 * the default color format of AnsiColors. change with {@link #setReset(Color, Color, boolean)}
	 */
	public static String colors = TerminalUtil.getPropertySafely("ot.ansi.colors").replace("$", ";");
	
	/**
	 * XTERM COLOR MODE. Change it with {@link #setColorMode(TermColors)}. The terminal once spawned should tell you what color mode it supports
	 */
	public static TermColors colorMode = setColorMode(TerminalUtil.getPropertySafely("ot.ansi.colors"));
	public static Palette picker;
	
	static
	{
		enableCmdColors();
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
		if(cls)
			cls();
	}
	
	/**
	 * set reset direct
	 */
	public static void setReset(String ansiEsc)
	{
		colors = ansiEsc;
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
	 * supports xterm-16, xterm-256 and true colors
	 */
	public static String formatColor(Color bg, Color text, String ansiEsc)
	{
		return formatColor(colorMode, bg, text, ansiEsc);
	}
	
	/**
	 * supports xterm-16, xterm-256 and true colors
	 */
	public static String formatColor(TermColors mode, Color bg, Color text, String ansiEsc)
	{
		if(colorMode == null)
		{
			System.err.println("colorMode isn't set yet! You have to wait to fetch it from the terminal or set it manually and try again!");
			return null;
		}
		
		switch(mode)
		{
			case TRUE_COLOR:
			{
				String b = bg == null ? "" : ESC + "[48;2;" + bg.getRed() + ";" + bg.getGreen() + ";" + bg.getBlue() + "m";
				String f = text == null ? "" : ESC + "[38;2;" + text.getRed() + ";" + text.getGreen() + ";" + text.getBlue() + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a;
			}
			case XTERM_256:
			{
				String b = bg == null ? "" : ESC + "[48;5;" + picker.pickColor(bg).code + "m";
				String f = text == null ? "" : ESC + "[38;5;" + picker.pickColor(text).code + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a;
			}
			case ANSI4BIT:
			{
				String b = bg == null ? "" : ESC + "[" + getANSI4BitColor((byte)picker.pickColor(bg).code, true) + "m";
				String f = text == null ? "" : ESC + "[" + getANSI4BitColor((byte)picker.pickColor(text).code, false) + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a;
			}
			default: 
				return null;
		}
	}
	
	/**
	 * @param code 0-15 4bit color
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
		ANSI4BIT(),//16 different colors for lazy coders/ scripters not knowing RGB
		XTERM_256(),//one byte colors (0-255) legacy
		TRUE_COLOR()//RGB 24 bit standard colors with each color having 8 bits(0-255)
	}
	
	/**
	 * clear screen using ANSI escape codes. Won't always work with conhost.exe(windows)
	 */
	public static void cls() 
	{
	    System.out.print(getCls());
	    System.out.flush();
	}
	
	public static String getCls()
	{
		String ANSI_CLS = AnsiColors.ESC + "[2J";
	    String ANSI_HOME = AnsiColors.ESC + "[H";
	    return AnsiColors.ESC + "[3J" + ANSI_CLS + ANSI_HOME;
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
			picker.parse(AnsiColors.class.getClassLoader().getResourceAsStream(colorMode == TermColors.ANSI4BIT ? "resources/jml/ot/colors/xterm-16.csv" : "resources/jml/ot/colors/xterm-256.csv"));
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public static TermColors setColorMode(String mode)
	{
		mode = mode.toLowerCase();
		TermColors colorMode = mode.equals("ansi4bit") ? TermColors.ANSI4BIT : mode.contains("true") && mode.contains("color") ? TermColors.TRUE_COLOR : TermColors.XTERM_256;
		setColorMode(colorMode);
		return colorMode;
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public static void setColorMode(TermColors mode)
	{
		colorMode = mode;
		if(mode != TermColors.TRUE_COLOR)
		{
			picker = new Palette();
			parsePalette();
		}
	}

	/**
	 * sets the colorMode equal to the terminals unless it's configured to use ANSI4bit
	 */
	public static void presetTerminal(String terminal, boolean ANSI4bit) 
	{
		if(ANSI4bit || TerminalUtil.getPropertySafely("ot.ansi.colors").toLowerCase().equals("ansi4bit"))
		{
			setColorMode(TermColors.ANSI4BIT);
			return;
		}
		if(TerminalUtil.windows_terminals.contains(terminal))
		{
			setColorMode(TermColors.TRUE_COLOR);//All windows terminals since they supported color support true color
		}
		//TODO: ICP to get the $TERMCOLOR and $TERM in case $TERMCOLOR is empty
	}
}
