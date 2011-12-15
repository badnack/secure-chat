import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.concurrent.locks.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Client {
    static final String LOCALHOST = "127.0.0.1";
    static final String KEYDIRECTORY = "/home/badnack/Projects/SecureChat/Ssl-Chat/KeyFiles/";
    static final int PORTC = 1234;

    public static void MakeDir(String name){
        try{
            Directory.MakeDirectory(KEYDIRECTORY + name);
        }catch(IOException x ){System.exit(-1);}

    }

    public static void main (String args[]){
        User user;
        String str;
        int port;
        ClientThread ct,cc;
        BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
        try{
            port = Integer.parseInt(args[0]);
            System.out.print("[CHAT] Nickname: ");
            str = stdIn.readLine();
            MakeDir(str);
            user = new User(str,port,LOCALHOST,KEYDIRECTORY);
            
            //Sets rsa parameters
            /*Checks whether file keys already exists, in other case has created*/
            user.CreateRsa();
            
            ct = new ClientThread (user,true);
            ct.start();
            
            /*Message board shared by two threads */
            while(true){
                System.out.print("[CHAT] Port to connect: ");
                str =  stdIn.readLine();
                
                /*i try these two ways because the thread server could receive
                  a connect request, in this case a message has shown.*/				
                if(str.compareTo("y") == 0){
                    if (!ct.IsConnected())continue;
                    ct.setAccepted(true);
                    ct.signalConnected();
                    break;
                }
                if(str.compareTo("n")==0){
                    if (!ct.IsConnected())continue;
                    ct.setAccepted(false);
                    ct.signalConnected();				
                    continue;
                }
                
                port = PORTC;
                try {
                    port = Integer.parseInt(str);
                }catch (Exception e) {
                    System.out.println("[Error] Unable to open the port given.");
                    continue;
                }

                
                /*Connection parameters*/
                user.setClientPort(port);
                user.setClientIp(LOCALHOST); // da cambiare con l'ip di destinazione
                
                
                /* If i try to connect i start the client mode, 
                   therefore i close the server thread */
                cc = new ClientThread (user,false);
                cc.start();
                break;
                
            }
            
        }
        
        catch(Exception e){
            System.out.println("[CHAT] Unhandled error!");
        }				
        
        
    } 
} 
