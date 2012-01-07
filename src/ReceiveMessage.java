import SecureChat.login.*;
import SecureChat.crypto.Rsa;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.security.*;

public class ReceiveMessage extends Thread {
    private  ObjectInputStream input;
    private PrivateKey PvKey;
    private User usr;
    private Rsa rsa;
    
    public ReceiveMessage (ObjectInputStream input,User usr){
        this.usr = usr;
        this.input = input;
    }

    public String getText (byte[] arr) throws UnsupportedEncodingException
    {
        String s = new String( arr, "UTF-8" );
        return s;
    }
    
    public void run(){
        byte[] data = null;
        String str = null;
        while (true){
            try{
                data = (byte[])input.readObject();
                str = usr.Decrypt(data);
                if(str==null)break;                
                System.out.println(usr.getUserName() + "> " + str);
                System.out.flush();
            }catch(Exception x){}            
        }
        System.out.println(usr.getUserName() + " has disconnected");
        System.exit(0);
    }
    

}
