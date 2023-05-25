package jml.reflect;


public class MCPSidedString {
	
	public String deob;
	public String ob;
	
	public MCPSidedString(String deob, String ob)
	{
		this.deob = deob;
		this.ob = ob;
	}
	
	@Override
	public String toString()
	{
		return this.deob;
	}

}
