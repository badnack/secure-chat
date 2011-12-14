import java.io.*;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.lang.*;
import java.math.BigInteger;
import java.util.concurrent.locks.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;
import java.net.*;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;




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
    static SecretKey desKey;
	
    public ClientThread (String name,int port, String ip, boolean type)throws IOException{
		this.sem = new ReentrantLock();
		this.type = type;
		this.port = port;
		this.ip = ip;
		this.MyName = name;
		WaitCall = sem.newCondition();
		accepted = false;
		//generate the DES key -- (si deve inserire in un try/catch e cmq va definita sopra 
		//						   come segreto condiviso tra i 2 client)
		//KeyGenerator keygen = KeyGenerator.getInstance("DES");
	    //desKey = keygen.generateKey();
		
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
	    Socket sslcsock;
	    ServerSocket ss;
	    boolean test;
	    String FName;
	    PrintStream StreamOut=null;
	    BufferedReader Buff=null;
	    BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
	    try{
	    	
	    /*	Cipher desCipherIn, desCipherOut;
	        // Create the cipher DES with CBC in EDE configuration, also with padding
	        desCipherIn = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	        //Initialize the cipher for encryption
	        desCipherIn.init(Cipher.ENCRYPT_MODE, desKey);
	        // to Encrypt the cleartext
	        //byte[] ciphertext = desCipher.doFinal(cleartext);
	        
	        // Create the cipher DES with CBC in EDE configuration, also with padding
	        desCipherOut = Cipher.getInstance("DESede/CBC/PKCS5Padding");
	        //Initialize the same cipher for decryption
	        desCipherOut.init(Cipher.DECRYPT_MODE, desKey);
	        // to Decrypt the ciphertext
	        //byte[] cleartext = desCipher.doFinal(ciphertext);
	    	*/
	    	
	    
		//server's body
		if(type){
		    while(true){
			resetConnect();
			
			/*SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
			SSLSocket sslcsock = (SSLSocket) sslserversocket.accept();*/
			
			ss = new ServerSocket(port);
			sslcsock = ss.accept();// Attesa socket
			if(IsConnected())System.exit(0); //whether the user has already connect the process has killed.
			StreamOut = new PrintStream (sslcsock.getOutputStream());
			Buff = new BufferedReader (new InputStreamReader (sslcsock.getInputStream()));
			System.out.print("[CHAT] "+Buff.readLine() + ": "); //accept message
			FName = Buff.readLine();
			sem.lock();
			connect = true;							
			WaitCall.await();//wait user decision (see other thread)				
			sem.unlock();
			/*
			// Some central authority creates new DH parameters
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
			paramGen.init(512);
			AlgorithmParameters params = paramGen.generateParameters();
			DHParameterSpec paramsSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
			
			/*
	         * Server creates her own DH key pair, using the DH parameters from
	         * above
	        *//*
			KeyPairGenerator serverKpairGen = KeyPairGenerator.getInstance("DH");
		    serverKpairGen.initialize(paramsSpec);
		    KeyPair serverKpair = serverKpairGen.generateKeyPair();
			
		    //Server creates and initializes his DH KeyAgreement object
		    
		    KeyAgreement serverKeyAgree = KeyAgreement.getInstance("DH");
	        serverKeyAgree.init(serverKpair.getPrivate());
	        
	        //Server encodes her public key, and sends it over to the Client
	        byte[] serverPubKeyEnc = serverKpair.getPublic().getEncoded();
	        //Send the Key to the client ----TO DO
	        //Wait for the client's public key  and then save it to "clientPubKeyEnc"--TO DO
	        
	        /*
	         * Server uses Client's public key for the first (and only) phase
	         * of her version of the DH
	         * protocol.
	         * Before she can do so, she has to instanticate a DH public key
	         * from Client's encoded key material.
	        */
	        /*
	        KeyFactory serverKeyFac = KeyFactory.getInstance("DH");
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc);
	        PublicKey clientPubKey = serverKeyFac.generatePublic(x509KeySpec);
	        serverKeyAgree.doPhase(clientPubKey, true);
	        
	        /*
	         * At this stage, both Server and Client have completed the DH key
	         * agreement protocol.
	         * Server generate the (same) shared secret.
	        *//*
	        byte[] serverSharedSecret = serverKeyAgree.generateSecret();
	        int serverLen = serverSharedSecret.length;
	        //send "serverLen" to the Client --- TO DO (i don't know why ihihihih)
		    */
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
		    
		    sslcsock = new Socket(ip,port);
		    /*SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		    SSLSocket sslcsock = (SSLSocket) sslsocketfactory.createSocket(ip, port);
		    StreamOut = new PrintStream (sslcsock.getOutputStream());*/
		    Buff = new BufferedReader (new InputStreamReader (sslcsock.getInputStream()));
		    
		    StreamOut.println(MyName + " : " + ip + " : " + port + " would talk with you, please press \'y\' to accept");
		    StreamOut.println(MyName);
		    
		    /*
	         * Client has received Server's public key
	         * in encoded format.
	         * He instantiates a DH public key from the encoded key material.
	        */
		    /*
		    //Client has received Server's public key and save it in "serverPubKeyEnc" ---TO DO
		    KeyFactory clientKeyFac = KeyFactory.getInstance("DH");
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc);
	        PublicKey serverPubKey = clientKeyFac.generatePublic(x509KeySpec);
	        
	        /*
	         * Client gets the DH parameters associated with Server's public key. 
	         * He must use the same parameters when he generates his own key
	         * pair.
	         *//*
	        DHParameterSpec dhParamSpec = ((DHPublicKey)serverPubKey).getParams();
	        
	        //Client creates his own DH key pair
	        KeyPairGenerator clientKpairGen = KeyPairGenerator.getInstance("DH");
	        clientKpairGen.initialize(dhParamSpec);
	        KeyPair clientKpair = clientKpairGen.generateKeyPair();
	        
	        //Client creates and initializes his DH KeyAgreement object
	        KeyAgreement clientKeyAgree = KeyAgreement.getInstance("DH");
	        clientKeyAgree.init(clientKpair.getPrivate());
	        
	     	//Client encodes his public key, and sends it over to Server.
	        byte[] clientPubKeyEnc = clientKpair.getPublic().getEncoded();
		    //Send the key to the server -- TO DO
	        
	        /*
	         * Client uses Server's public key for the first (and only) phase
	         * of his version of the DH
	         * protocol.
	         *//*
	        clientKeyAgree.doPhase(serverPubKey, true);
	        
	        /*
	         * At this stage, both Server and Client have completed the DH key
	         * agreement protocol.
	         * Client generate the (same) shared secret.
	         */
	        //receive "serverLen from Server and save it in "serverLen" --- TO DO
	        /*byte[] clientSharedSecret = new byte[serverLen];
	        int clientLen = clientKeyAgree.generateSecret(clientSharedSecret, 0);
	        */
	        
	        
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
