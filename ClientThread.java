import java.io.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.locks.*;
import java.net.*;
import java.security.*;

public class ClientThread extends Thread {
    static boolean  connect;
    static boolean accepted;
    static Lock  sem ;
    static Condition WaitCall; 	
    private boolean type;
    private int port;
    private String ip;
    private String MyName;
	
    public ClientThread (String name,int port, String ip, boolean type)throws IOException{
        this.sem = new ReentrantLock();
        this.type = type;
        this.port = port;
        this.ip = ip;
        this.MyName = name;
        WaitCall = sem.newCondition();
        accepted = false;
    }
    
    public boolean IsConnected(){
        boolean test;
        sem.lock();
        test = connect;
        sem.unlock();
        return test;
    }

    public void signalConnected(){
        sem.lock();
        try{
            WaitCall.signal();
        }
        finally{
            sem.unlock();
        }	
    }
    
    public void exitThread(){
        System.exit(0);	
    }
    
    private void resetConnect(){
        sem.lock();
        connect = false;							
        sem.unlock();
    }
    
    private void setConnect(){
        sem.lock();
        connect = true;							
        sem.unlock();
    }
    
    public void setAccepted(boolean value){
        accepted = value;
    }
    public boolean isAccepted(){
        return accepted;
    }

    public String getText (byte[] arr) throws UnsupportedEncodingException
    {
        String s = new String( arr, "UTF-8" );
        return s;
    }
    
    public void run(){
        Socket csock;
        ServerSocket ss;
        boolean test;
        String FName = null;
        ObjectOutputStream StreamOut=null;
        byte[] Buff = null;
        ObjectInputStream ois;
        BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
        try{
            
            //server's body
            if(type){
                while(true){
                    resetConnect();
                                        
                    ss = new ServerSocket(port);
                    csock = ss.accept();// Attesa socket
                    if(IsConnected())System.exit(0); //whether the user has already connect the process has killed.
                   
                    StreamOut = new ObjectOutputStream( csock.getOutputStream() );
                    ois = new ObjectInputStream( csock.getInputStream() );
                    Buff = (byte[]) ois.readObject();
                    
                    FName = getText(Buff);
                    System.out.print("[CHAT] "+ FName + ": "); //accept message
                    Buff = (byte[]) ois.readObject();
                    FName = getText(Buff);
                    
                    sem.lock();
                    connect = true;							
                    WaitCall.await();//wait user decision (see other thread)				
                    sem.unlock();
                    
                    if(!isAccepted())StreamOut.writeObject(("NACK").getBytes());						
                    else {
                        StreamOut.writeObject(MyName.getBytes());
                        System.out.println("[CHAT] Connected with " + FName);
                        break;
                    }
                }
                
            }
            
            //client's body
            //receive the name on connect success
            else{
                System.out.println("Trying to connect on port: " + port);
                if(IsConnected())System.exit(0); //whether the server mode in on, the client mode have to be closed
                csock = new Socket(ip,port);
                StreamOut = new ObjectOutputStream( csock.getOutputStream() );
                ois = new ObjectInputStream( csock.getInputStream() );
                
                StreamOut.writeObject(("\n" + MyName + " : " + ip + " : " + port + " would talk with you, please press \'y\' to accept").getBytes());    
                StreamOut.writeObject(MyName.getBytes());

                Buff = (byte[]) ois.readObject();
                FName = getText(Buff);

                if(FName.compareTo("NACK")==0){
                    System.out.println("[CHAT] Connection not accepted");
                    System.exit(0);
                }

                else System.out.println("[CHAT] Connected with " + FName);
                
            }
            
            /*Retrieves both keys to send/receive messages*/
            PublicKey PuKey = Rsa.GetPublicKey(MyName,FName);
            PrivateKey PvKey = Rsa.GetPrivateKey(MyName);
           
            /*receive messages*/
            new ReceiveMessage(ois,FName,PvKey).start();
            
            /*send messages*/
            while (true)
                StreamOut.writeObject(Rsa.Encrypt(stdIn.readLine(),PuKey));
                
        }
        catch (Exception e) {
            System.out.println("[Error] User appears to be offline");
            System.exit(0);
        }	
    }
}
