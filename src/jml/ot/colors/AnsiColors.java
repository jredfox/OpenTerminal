package jml.ot.colors;

import java.awt.Color;

import jml.ot.OTConstants;
import jredfox.common.utils.Assert;

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
	public static final String DEFAULT_FG = ESC + "[39";
	public static final String DEFAULT_BG = ESC + "[49";
	/**
	 * use this color when formatting or printing to use the CLI's(Terminal's) default Text Color
	 */
	public static final Color COLOR_DEFAULT = new Color(0,0,0);
	/**
	 * not supported in windows
	 */
	public static final String FRAMED = ESC + "[51m";
	/**
	 * not supported in windows
	 */
	public static final String ENCIRCLED = ESC + "[52m";
	public static final String OVERLINE = ESC + "[53m";
	public static final Palette pickerXterm256 = new Palette("resources/jml/ot/colors/xterm-256.csv");
	public static final Palette pickerWin4Bit = new Palette("resources/jml/ot/colors/ansi4bit-windows-10.csv");
	public Palette pickerAnsi4Bit = null;
	/**
	 * the AnsiColor instance used to be a static utility if your un-interested in using multiple instances
	 * NOTE: this instance doesn't sync with your TerminalApp's colors. Therefore the colormode, color, and ansi4bit color palette will be unknown
	 */
	public static final AnsiColors INSTANCE = new AnsiColors();
	/**
	 * XTERM COLOR MODE. Change it with {@link #setColorMode(TermColors)}. The terminal once spawned should tell you what color mode it supports
	 */
	public TermColors colorMode;
	/**
	 * the default color format of AnsiColors. change with {@link #setReset(Color, Color, boolean)}
	 */
	public String colors;
	protected Color currentBg;
	protected Color currentFg;
	protected String currentAnsiEsc;
	
	public AnsiColors()
	{
		this.colors = "";
		this.pickerAnsi4Bit = pickerWin4Bit;
	}
	
	public AnsiColors(Color background, Color text, String ansiEsc, TermColors mode)
	{
		this.setReset(background, text, ansiEsc, false);
		this.setColorMode(mode);
	}
	
	public static enum TermColors
	{
		ANSI4BIT(),//16 different colors for dinosaur graphics cards or lazy scripters
		XTERM_256(),//one byte colors (0-255) legacy
		TRUE_COLOR(),//RGB 24 bit standard colors with each color having 8 bits(0-255)
		TRUE_COLOR_RGBA()//true color with transparency added currently no terminal supports this
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public void setColorMode(TermColors mode)
	{
		if(mode == null) 
			return;
		
		this.colorMode = mode;
	}
	
	/**
	 * switch the ANSI terminal mode from one mode to another
	 */
	public void setColorMode(String mode)
	{
		this.setColorMode(this.getColorMode(mode));
	}
	
	/**
	 * get's a color mode from string. Assumes XTERM-256 if not specified
	 */
	public TermColors getColorMode(String mode) 
	{
		mode = mode.toLowerCase();
		TermColors colorMode = mode.equals("ansi4bit") ? TermColors.ANSI4BIT : (mode.equalsIgnoreCase("nullnull") || mode.contains("true") && mode.contains("color") || mode.contains("24") && mode.contains("bit")) ? TermColors.TRUE_COLOR : TermColors.XTERM_256;
		return colorMode;
	}
	
	/**
	 * sets the color mode and syncs {@link #colors} to the new format from {@link #currentBg} {@link #currentFg} {@link #currentAnsiEsc} values
	 */
	public void updateColorMode(String newMode, boolean cls)
	{
		this.updateMode(this.getColorMode(newMode), cls);
	}
	
	/**
	 * sets the color mode and syncs {@link #colors} to the new format from {@link #currentBg} {@link #currentFg} {@link #currentAnsiEsc} values
	 */
	public void updateMode(TermColors newMode, boolean cls)
	{
		this.setColorMode(newMode);
		this.setReset(this.currentBg, this.currentFg, this.currentAnsiEsc, cls);
	}

	/**
	 * get the reset ansi esq for the whole program. doing esq[0m hard coded will cause the entire app to be reset back to default formating no additional styling
	 */
	public String getReset()
	{
		return RESET + this.colors;
	}
	
	/**
	 * this will return the default ansi escape reset sequence without the colors this will override the entire console app's settings if used
	 */
	public String getHardReset()
	{
		return RESET;
	}
	
	public void setReset(Color background, Color text, boolean cls)
	{
		this.setReset(background, text, "", cls);
	}
	
	public void setReset(Color background, Color text, String ansiEsc, boolean cls)
	{
		this.colors = this.formatColor(background, text, ansiEsc, false);
		System.out.print(this.getReset() + (cls ? getCls() : ""));//in order for terminals to update the background and ansi text effects the screen has to be cleared
		System.out.flush(); //ensure the color is set to the terminal before clearing the screen
		//preserve original values in case the color mode changes again
		this.currentBg = background;
		this.currentFg = text;
		this.currentAnsiEsc = ansiEsc;
	}

	public void print(Color background, Color textColor, String str)
	{
		System.out.print(this.formatColor(background, textColor, str));
	}
	
	public void print(ANSI4BitColor background, ANSI4BitColor textColor, String str)
	{
		System.out.print(this.formatANSI4BitColor(background, textColor, str));
	}
	
	public void println(Color background, Color textColor, String str)
	{
		this.print(background, textColor, str);
		System.out.println();
	}
	
	public void println(ANSI4BitColor background, ANSI4BitColor textColor, String str)
	{
		this.print(background, textColor, str);
		System.out.println();
	}
	
	/**
	 * supports ansi4bit, xterm-256 and true colors
	 */
	public String formatColor(Color bg, Color textColor, String ansiEsc)
	{
		return this.formatColor(bg, textColor, ansiEsc, true);
	}
	
	/**
	 * supports ansi4bit, xterm-256 and true colors
	 */
	public String formatColor(Color bg, Color textColor, String ansiEsc, boolean reset)
	{
		return formatColor(this.colorMode, bg, textColor, ansiEsc, reset);
	}
	
	/**
	 * supports xterm-16, xterm-256 and true colors
	 */
	public String formatColor(TermColors mode, Color bg, Color textColor, String ansiEsc, boolean reset)
	{
		Assert.is(mode != null, "AnsiColors#colorMode is null!");
		String r = reset ? this.getReset() : "";
		
		switch(mode)
		{
			case TRUE_COLOR:
			{
				String b = bg == null ? "" : bg == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), true) + "m" : ESC + "[48;2;" + bg.getRed() + ";" + bg.getGreen() + ";" + bg.getBlue() + "m";
				String f = textColor == null ? "" : textColor == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), false) + "m" : ESC + "[38;2;" + textColor.getRed() + ";" + textColor.getGreen() + ";" + textColor.getBlue() + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a + r;
			}
			case XTERM_256:
			{
				String b = bg == null ? "" : bg == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), true) + "m" : ESC + "[48;5;" + pickerXterm256.pickColor(bg).code + "m";
				String f = textColor == null ? "" : textColor == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), false) + "m" : ESC + "[38;5;" + pickerXterm256.pickColor(textColor).code + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a + r;
			}
			case ANSI4BIT:
			{
				String b = bg == null ? "" : bg == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), true) + "m" : ESC + "[" + getANSI4BitColor((byte)pickerAnsi4Bit.pickColor(bg).code, true) + "m";
				String f = textColor == null ? "" : textColor == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), false) + "m" : ESC + "[" + getANSI4BitColor((byte)pickerAnsi4Bit.pickColor(textColor).code, false) + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a + r;
			}
			case TRUE_COLOR_RGBA:
			{
				String b = bg == null ? "" : bg == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), true) + "m" : ESC + "[48;0;" + bg.getRed() + ";" + bg.getGreen() + ";" + bg.getBlue() + ";" + bg.getAlpha() + "m";
				String f = textColor == null ? "" : textColor == COLOR_DEFAULT ? ESC + "[" + getANSI4BitColor((byte)ANSI4BitColor.DEFAULT_COLOR.ordinal(), false) + "m" : ESC + "[38;0;" + textColor.getRGB() + "m";
				String a = ansiEsc == null ? "" : ansiEsc;
				return b + f + a + r;
			}
			default:
				return null;
		}
	}
	
	/**
	 * format your ANSI4BITColor without lossy RGB conversions. this will guarantee the color you specify displays as intended by the CLI / CLI's profile
	 */
	public String formatANSI4BitColor(ANSI4BitColor bg, ANSI4BitColor textColor, String str)
	{
		return this.formatANSI4BitColor(bg, textColor, str, true);
	}
	
	/**
	 * format your ANSI4BITColor without lossy RGB conversions. this will guarantee the color you specify displays as intended by the CLI / CLI's profile
	 */
	public String formatANSI4BitColor(ANSI4BitColor bg, ANSI4BitColor textColor, String str, boolean reset)
	{
		String r = reset ? this.getReset() : "";
		String b = bg == null ? "" : ESC + "[" + getANSI4BitColor((byte)bg.ordinal(), true) + "m";
		String f = textColor == null ? "" : ESC + "[" + getANSI4BitColor((byte)textColor.ordinal(), false) + "m";
		String a = str == null ? "" : str;
		return b + f + a + r;
	}
	
	public enum ANSI4BitColor
	{
		BLACK,
		RED,
		GREEN,
		YELLOW,
		BLUE,
		MAGENTA,
		CYAN,
		WHITE,
		/**
		 * grey in most terminal's color palettes but could also be a brighter(lighter) black
		 */
		BRIGHT_BLACK,
		BRIGHT_RED,
		BRIGHT_GREEN,
		BRIGHT_YELLOW,
		BRIGHT_BLUE,
		BRIGHT_MAGENTA,
		BRIGHT_CYAN,
		BRIGHT_WHITE,
		DEFAULT_COLOR
	}

	/**
	 * @param code 0-15 4bit color or 16 for default color
	 * @param background(boolean)
	 * @return the actual ANSI ESC code you need to use for that specific 4 bit color
	 */
	public static int getANSI4BitColor(byte code, boolean bg)
	{
		if(code > 15)
			return bg ? 49 : 39;//if code is 16 return default color vars
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
	
	/**
	 * clear screen using ANSI escape codes
	 */
	public static void cls()
	{
	    System.out.print(getCls());
	    System.out.flush();
	}
	
	public static String getCls()
	{
		return "\033[H\033[2J\033[3J"; //TODO: after IPC is done test if the launch Esc[3J is necessary
	}
	
	/**
	 * hack windows 10 conhost.exe and wt to enable colors straight from java
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
			}
			catch (Exception e){e.printStackTrace();}
		}
	}
}
