package jml.reflect;


public class ObfString {
	
	public String deob;
	public String ob;
	
	public ObfString(String deob, String ob)
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
