package jml.ot;

public class Debug {

	public static void main(String[] args)
	{
//		String s = "\"command with spaces\" arg0 \"arg1 with\\\"quotes\\'\" arg\\t2 \"ar\"\"g3\"";
		String s = "\"arg\"\"0\" arg1 'Testing\"\"...' \"HI''''...\"";
//		String s = "command \"\" \"\" \" \"";
//		System.out.println(s);
		String[] arr = TerminalUtil.parseCommand(s);
		for(String a : arr)
			System.out.println("\"" + a + "\"");
	}
	
}
