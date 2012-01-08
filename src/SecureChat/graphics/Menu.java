/**
 *  Menu.java
 *  
 *  @author Nilo Redini
 *  @auhor Davide Pellegrino
 *
 *  This class merely shows a little menu 
 * 
*/

package SecureChat.graphics;
import java.io.*;

public class Menu{

    /** Prints a simple initial menu whereby user can choose what to do 
        @return int : User choice ( 1 login , 0 registration) 
        @throws IOException
     */
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

    /**
       Prints a Login menu
       @return String[] : Credentials ( in order user name and password )
       @throws IOException
     */
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

    /**
       Prints a simple registration menu
       @return String[] : Credentials ( in order user name and password )
       @throws IOException
     */
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


    /**
       Pritns the main chat board screen
       @param toShow : additional informations to show
     */
    public void ChatBoard(String toShow){
        System.out.print("\033c");
        System.out.println("************************");
        System.out.println("*** Secure Chat v2.2 ***");
        System.out.println("************************");
        System.out.println(toShow);
        System.out.println("");
       
    }

}