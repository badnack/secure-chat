import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

class RemoteFile{
    public static boolean SendFile(String path,ObjectOutputStream StreamOut){
        try{
            FileInputStream fis = new FileInputStream(path);
            byte [] packet = new byte[ fis.available() ];
            fis.read( packet );
            fis.close();
            StreamOut.writeObject( packet );
        }catch(Exception x){return false;}
        
        return true;
    }

    public static boolean ReceiveFile(String path , ObjectInputStream StreamIn){
        try{
            byte[] packet = (byte[]) StreamIn.readObject();
            FileOutputStream fos = new FileOutputStream( path );
            fos.write( packet );
            fos.close();
        }catch(Exception x){return false;}
        return true;
    }
}