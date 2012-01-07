package SecureChat.file;

import java.io.*;

public class Directory{
    public static final String LOCALHOST = "127.0.0.1";
    public static final String KEYDIRECTORY = "/home/badnack/Projects/SecureChat/SecureChat/secure-chat/test/KeyFiles/";
    //public static final String KEYDIRECTORY = "/home/davide/SecureChat/KeyFiles";
    public static final String PATHDH = "/home/badnack/Projects/SecureChat/SecureChat/secure-chat/test/KeyFiles/PrimeDH/Prime";
    public static final String CREDENTIALSPATH = "/home/badnack/Projects/SecureChat/SecureChat/secure-chat/test/Credentials/";
    //public static final String CREDENTIALSPATH = "/home/davide/SecureChat/Credentials";

    public static void MakeDirectory(String path) throws IOException{
        File dir=new File(path);
        if(dir.isDirectory())return;
        if(dir.exists())return;
        if(dir.mkdirs())return;		
        throw new IOException("Unable to create a new folder right now");
		
    }
}