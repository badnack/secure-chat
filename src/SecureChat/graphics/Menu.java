package SecureChat.graphics;
import java.io.*;

public class Menu{
    public static int InitialMenu() throws IOException{
        char get;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            System.out.print("\033c");
            System.out.println("************************");
            System.out.println("*** Secure Chat v2.2 ***");
            System.out.println("************************");

            System.out.println("n: New User");
            System.out.println("r: Registered User");
            System.out.print("?");
            get = (char)in.read();
            if (get == 'n') return 0;
            if (get == 'r') return 1;             
        }
    }

    public String[] RegisteredMenu() throws IOException{
        String [] cred = new String[2];
        BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
        
        System.out.print("\033c");        
        System.out.println("************************");
        System.out.println("******** Login *********");
        System.out.println("************************");


        System.out.print("User name: ");
        cred[0] = stdIn.readLine();

        System.out.print("Password: ");
        cred[1] = stdIn.readLine();
        return cred;
    }

    //controllare password vuota
    public String[] NewUserMenu()throws IOException{
        String [] cred = new String[2];

        BufferedReader stdIn = new BufferedReader ( new InputStreamReader (System.in));
        
        System.out.print("\033c");        
        System.out.println("************************");
        System.out.println("***** Registration *****");
        System.out.println("************************");
            
        System.out.print("New user name: ");
        cred[0] = stdIn.readLine();
        
        System.out.print("Password: ");
        cred[1] = stdIn.readLine();
        return cred;
    }
    
    public void ChatBoard(String toShow){
        System.out.print("\033c");
        System.out.println("************************");
        System.out.println("*** Secure Chat v2.2 ***");
        System.out.println("************************");
        System.out.println(toShow);
        System.out.println("");
       
    }

}