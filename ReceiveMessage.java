
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public class ReceiveMessage extends Thread {
	private BufferedReader input;
	
	public ReceiveMessage (BufferedReader input){
		this.input = input;
	}

	public void run(){
		try{
			while (true){
				String str1 = input.readLine();
				System.out.println(str1);
			}
		}
		catch (IOException e) {
			System.out.println("Error on listening");
		}
	}
}
