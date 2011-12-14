
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ReceiveMessage extends Thread {
    private  ObjectInputStream input;
    private String name;

    public ReceiveMessage (ObjectInputStream input,String name){
        this.input = input;
        this.name = name;
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
                str = Rsa.Decrypt(data,name);
                if(str==null)break;                
                System.out.println(name + "> " + str);
                System.out.flush();
            }catch(Exception x){}            
        }
        System.out.println(name + " has disconnected");
        System.exit(0);
    }
    

}
