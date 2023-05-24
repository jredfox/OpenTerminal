package jredfox.common.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUtils {
	
	public static void create(File f)
	{
		try
		{
			if(!f.exists())
			{
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getTrueName(File file)
	{
		String name = file.getName();
		if(file.isDirectory())
			return name;
		int index = name.lastIndexOf('.');
		return index != -1 ? name.substring(0, index) : name;
	}
	
	public static String getRealtivePath(File dir, File file) 
	{
		String path = file.getPath();
		String dirPath = dir.getPath();
		boolean plus1 = path.contains(File.separator);
		return path.substring(path.indexOf(dirPath) + dirPath.length() + (plus1 ? 1 : 0));
	}
	
	public static String getExtensionFull(File file) 
	{
		String ext = getExtension(file);
		return ext.isEmpty() ? "" : "." + ext;
	}

	/**
	 * get a file extension. Note directories do not have file extensions
	 */
	public static String getExtension(File file) 
	{
		String name = file.getName();
		int index = name.lastIndexOf('.');
		return index != -1 && !file.isDirectory() ? name.substring(index + 1) : "";
	}

	public static URL toURL(File f)
	{
		try 
		{
			return f.toURI().toURL();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

}
