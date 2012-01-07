/**
   Client.java
   @author Nilo Redini
   @author Davide Pellegrino
   
   This class contain the main function and implements
   the main part of a chat.
   Here user can register or load his credentials, moreover he can connecting
   to another user or viceversa.
*/
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.concurrent.locks.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import SecureChat.login.*;
import SecureChat.graphics.*;
import SecureChat.file.*;

public class Client {
    static final int PORTC = 1234;
    
    public static void MakeDir(String name){
        try{
            Directory.MakeDirectory(Directory.KEYDIRECTORY + name);
        }catch(IOException x ){
            System.out.println(x.getMessage());
            System.exit(-1);
        }

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
            while(true){
                ch = menu.InitialMenu();
                /* Registration */
                if(ch == 0) {
                    cred = menu.NewUserMenu();
                    if(cred[1].length()<2){
                        System.out.println("Warning: Password is too short.");
                        Thread.sleep(1000);
                        continue;
                    }
                    if(!login.newUser(cred[0],cred[1])){
                        System.out.println("Warning: User name already in use.");
                        Thread.sleep(1000);
                        continue;
                    }
                    
                }
                
                /* Login */
                if(ch == 1) {
                    cred = menu.RegisteredMenu();
                    if(!login.LoadUser(cred[0],cred[1])){
                        System.out.println("Error: User or Password are invalid.");
                        Thread.sleep(1000);                                            
                        continue;
                    }
                    break;
                    
                }                       
            }
            /*Checks whether the user directory exists*/
            MakeDir(cred[0]);
            /*Loads user credential*/
            user = new User(port,Directory.LOCALHOST,login);
            if(!user.isValid()) System.exit(0);
            /*Sets rsa parameters.
            Checks whether file keys already exists, in other case has created */
            if(!user.CreateRsa(Directory.KEYDIRECTORY))System.exit(0);
            
            /*Shows chat board, showing the port given*/
            menu.ChatBoard("Listening port: " + Integer.toString(port));

            ct = new ClientThread (user,true);
            ct.start();
            
            /*Message board shared by two threads */
            while(true){
		
                System.out.print("[CHAT] Select IP_Address:Port to connect : ");
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
		// split the IP address and the port, then copy in cred[0] and cred[1]
		cred=str.split(":");	
                try {
                    port = Integer.parseInt(cred[1]);
                }catch (Exception e) {
                    System.out.println("[Error] Unable to open the port given.");
                    continue;
                }

                
                /*Connection parameters*/
                user.setClientPort(port);
                user.setClientIp(cred[0]); // da cambiare con l'ip di destinazione
                
                
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
