
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
	    while (true){
		String str1 = input.readLine();		
		System.out.println(name + "> " + str1);
	    }
	}
	catch (IOException e) {
		System.out.println("Error on listening");
	}
    }
}
