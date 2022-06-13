package jml.ot.colors;

import java.awt.Color;
import java.io.IOException;

import jml.ot.OTConstants;
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
	/**
	 * the AnsiColor instance used to be a static utility if your un-interested in using multiple instances
	 * NOTE: this doesn't sync with terminals you create if your wanting custom {@link #colors} formats or using the right {@link #colorMode} you have to sync it manually
	 */
	public static final AnsiColors INSTANCE = new AnsiColors();
	public static final Palette pickerXterm256 = new Palette("resources/jml/ot/colors/xterm-256.csv");
	public static final Palette pickerAnsi4Bit = new Palette("resources/jml/ot/colors/xterm-16.csv");
	
	/**
	 * the default color format of AnsiColors. change with {@link #setReset(Color, Color, boolean)}
	 */
	public String colors = TerminalUtil.getPropertySafely("ot.ansi.colors").replace("$", ";");
	
	/**
	 * XTERM COLOR MODE. Change it with {@link #setColorMode(TermColors)}. The terminal once spawned should tell you what color mode it supports
	 */
	public TermColors colorMode = setColorMode(TerminalUtil.getPropertySafely("ot.ansi.colors"));
	
	public AnsiColors()
	{
		
	}
	
	public AnsiColors(Color background, Color text, String ansiEsc, TermColors mode)
	{
		this.setReset(background, text, ansiEsc, false);
		this.setColorMode(mode);
	}

	/**
	 * get the reset ansi esq for the whole program. doing esq[0m hard coded will cause the entire app to be reset back to default formating no additional styling
	 */
	public String getReset()
	{
		return RESET + colors;
	}
	
	/**
	 * this will return the default ansi escape reset sequence without the colors this will override the entire console app's settings if used
	 */
	public String getNonColoredReset()
	{
		return RESET;
	}
	
	public void setReset(Color background, Color text, boolean cls)
	{
		setReset(background, text, "", cls);
	}
	
	public void setReset(Color background, Color text, String ansiEsc, boolean cls)
	{
		colors = formatColor(background, text, ansiEsc);
		System.out.print(getReset());
		System.out.flush();
		if(cls)
		  cls();
	}
	
	/**
	 * set reset direct
	 */
	public void setReset(String ansiEsc)
	{
		colors = ansiEsc;
	}

	public void print(Color background, Color text, String str)
	{
		System.out.print(formatColor(background, text, "") + str + getReset());
	}
	
	public void println(Color background, Color text, String str)
	{
		System.out.println(formatColor(background, text, str) + getReset());
	}
	
	/**
	 * supports xterm-16, xterm-256 and true colors
	 */
	public String formatColor(Color bg, Color text, String ansiEsc)
	{
		return formatColor(colorMode, bg, text, ansiEsc);
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public TermColors setColorMode(String mode)
	{
		mode = mode.toLowerCase();
		TermColors colorMode = mode.equals("ansi4bit") ? TermColors.ANSI4BIT : mode.contains("true") && mode.contains("color") ? TermColors.TRUE_COLOR : TermColors.XTERM_256;
		setColorMode(colorMode);
		return colorMode;
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public void setColorMode(TermColors mode)
	{
		if(mode == null) 
			return;
		
		colorMode = mode;
	}
	
	//START STATIC UTILITIES
	
	/**
	 * supports xterm-16, xterm-256 and true colors
	 */
	public static String formatColor(TermColors mode, Color bg, Color text, String ansiEsc)
	{
		if(mode == null)
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
				String b = bg == null ? "" : ESC + "[48;5;" + pickerXterm256.pickColor(bg).code + "m";
				String f = text == null ? "" : ESC + "[38;5;" + pickerXterm256.pickColor(text).code + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a;
			}
			case ANSI4BIT:
			{
				String b = bg == null ? "" : ESC + "[" + getANSI4BitColor((byte)pickerAnsi4Bit.pickColor(bg).code, true) + "m";
				String f = text == null ? "" : ESC + "[" + getANSI4BitColor((byte)pickerAnsi4Bit.pickColor(text).code, false) + "m";
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
	
	/**
	 * updated based on new stackoverflow readings... seems to work more consistently then ansi home + ansi clear 2J
	 */
	public static String getCls()
	{
//	    return "\33c" + ESC + "[3J";
		return "\033[H\033[2J";
	}
	
	/**
	 * hack windows 10 conhost.exe to enable colors straight from java
	 */
	public static void enableCmdColors()
	{
		String prop = System.getProperty("ot.w");
		if(OTConstants.LAUNCHED && prop != null)
		{
			boolean cmd = prop.equals("true");
			try
			{
				if(cmd)
					new ProcessBuilder(new String[]{"cmd", "/c", "echo | set /p dummyName=\"\""}).inheritIO().start().waitFor();
				else
					new ProcessBuilder(new String[]{"powershell", "/c", "Write-Output \"$null\""}).inheritIO().start().waitFor();
			}
			catch (Exception e){e.printStackTrace();}
		}
	}
}
