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
        Menu menu= new Menu();
        SecureLogin login = new SecureLogin();
        String str = new String();
        String []cred = new String[2]; /*cred[0] is the user name, cred[2] is the password*/
        int port;
        int ch;
        ClientThread ct,cc;
        BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
        try{
            port = Integer.parseInt(args[0]);
            ch = menu.InitialMenu();
            /* Registration */
            if(ch == 0) {
                while(true){
                    cred = menu.NewUserMenu();
                    if(cred[1].length()<2){
                        System.out.println("Warning: Password is too short.");
                        //mettere una sleep
                        continue;
                    }
                    if(!login.newUser(cred[0],cred[1])){
                        System.out.println("Warning: User name already in use.");
                        //mettere una sleep
                    }
                    else break;
                }                              
            }

            /* Login */
            if(ch == 1) {
                cred = menu.RegisteredMenu();
                if(!login.LoadUser(cred[0],cred[1])){
                    System.out.println("Error: User or Password are invalid.");
                    System.exit(0);
                }

            }
                        
            
            /*Checks whether the user directory exists*/
            MakeDir(cred[0]);
            /*Loads user credential*/
            user = new User(cred[0],port,LOCALHOST,KEYDIRECTORY);
            
            /*Sets rsa parameters.
            Checks whether file keys already exists, in other case has created*/
            user.CreateRsa();
            
            /*Shows chat board, showing the port given*/
            menu.ChatBoard("Listening port: " + Integer.toString(port));

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
                    menu.ChatBoard("");
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
