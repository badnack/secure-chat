import java.io.*;

public class Directory{
	public static void MakeDirectory(String path) throws IOException{
		File dir=new File(path);
		if(dir.isDirectory())return;
		if(dir.exists())return;
		if(dir.mkdirs())return;		
	 	throw new IOException("Unable to create a new folder right now");
		
	}
}