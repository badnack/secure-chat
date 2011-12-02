
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
	    while ((str1 = input.readLine())!=null){			     
		System.out.println(name + "> " + str1);
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
