/**
 *   Directory.java
 *
 *   @author Nilo Redini
 *   @author Davide Pellegrino
 *
 *   this class is used to create (if this doesn't exists) a directory
*/

package SecureChat.file;

import java.io.*;

public class Directory{
    public static final String LOCALHOST = "127.0.0.1";

    /**
       Allows to create a new directory
       @param path : path of directory
       @throws IOException
     */
    public static void MakeDirectory(String path) throws IOException{
        File dir=new File(path);
        if(dir.isDirectory())return;
        if(dir.exists())return;
        if(dir.mkdirs())return;		
        throw new IOException("Unable to create a new folder right now");
		
    }
}
