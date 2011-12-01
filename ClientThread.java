import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.locks.*;

public class ClientThread extends Thread {
	private final int TIMEOUT = 500;
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

	public void run(){
		Socket csock;
		ServerSocket ss;
		boolean test;
		String FName;
		PrintStream StreamOut=null;
		BufferedReader Buff=null;
		BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
		try{
			//server's body
			if(type){
				while(true){
					resetConnect();
					ss = new ServerSocket(port);
					csock = ss.accept();// Attesa socket
					if(IsConnected())System.exit(0); //whether the user has already connect the process has killed.
					StreamOut = new PrintStream (csock.getOutputStream());
					Buff = new BufferedReader (new InputStreamReader (csock.getInputStream()));
					System.out.print("[CHAT] "+Buff.readLine() + ": "); //accept message
					FName = Buff.readLine();
					sem.lock();
					connect = true;							
					WaitCall.await();//wait user decision (see other thread)				
					sem.unlock();
					if(!isAccepted())StreamOut.println("NACK");						
					else {
						StreamOut.println(MyName);
						System.out.println("[CHAT] Connected with " + FName);
						break;
					}
				}
							
			}

			//client's body
			//receive the name on connect success
			else{
				System.out.println("Trying to connect on port: " + port);
				if(IsConnected())System.exit(0);
				csock = new Socket(ip,port);
				StreamOut = new PrintStream (csock.getOutputStream());
				Buff = new BufferedReader (new InputStreamReader (csock.getInputStream()));
				StreamOut.println(MyName + " : " + ip + " : " + port + " would talk with you, please press \'y\' to accept");
				StreamOut.println(MyName);
				FName = Buff.readLine();
				if(FName.compareTo("NACK")==0){
					System.out.println("Connection not accepted");
					System.exit(0);
				}
				else System.out.println("[CHAT ]Connected with " + FName);
							
			}
			
			/*receive messages*/
			new ReceiveMessage(Buff,FName).start();
			
			/*send messages*/
			while (true)
				StreamOut.println(stdIn.readLine());
		}
		catch (Exception e) {
			System.out.println("[Error] User appears to be offline");
			System.exit(0);
		}	
	}
}
