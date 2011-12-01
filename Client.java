import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.concurrent.locks.*;

public class Client {
	
static final int PORTC = 1234;

public static void main (String args[]){
	String str;
	String name;
	int port;
	ClientThread ct,cc;
	BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
	try{
		/* Accept thread*/
		port = Integer.parseInt(args[0]);
		System.out.print("[CHAT] Nickname: ");
		name = stdIn.readLine();
		ct = new ClientThread (name,port,"127.0.0.1",true);
		ct.start();
		
		/*Message board shared by two thread*/
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
			} 

			catch (Exception e) {
				System.out.println("[Error] Unable to open the port given.");
				continue;
			}
			
			
			/* If i try to connect i close the server thread */
			cc = new ClientThread (name,port,"127.0.0.1",false);
			cc.start();
			break;
			
		}

	}

	catch(Exception e){
		System.out.println("Eccezione");
	}				
	

} // end metodo

} // end class
