
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ReceiveMessage extends Thread {
    private BufferedReader input;
    private String name;

    public ReceiveMessage (BufferedReader input,String name){
        this.input = input;
        this.name = name;
    }
    
    public void run(){
        try{
            String str1 = null;
            String str2 = null;
            while (true){
                str1 = input.readLine();
                if(str1==null)break;
                try{
                    str2 = Rsa.Decrypt(str1.getBytes(),name);
                }catch(Exception x){}
                System.out.println(name + "> " + str2);
                System.out.flush();
            }
            System.out.println(name + " has disconnected");
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println("Error on listening");
        }
    }
}
