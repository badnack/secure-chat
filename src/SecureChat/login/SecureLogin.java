package SecureChat.login;

import SecureChat.file.*;
import java.security.SecureRandom;
import java.util.Random;
import java.io.*;
import java.net.*;
import java.lang.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Enumeration;

public class SecureLogin{
    static final int ITERATION = 1000;
    static final String FILENAME = "passwd";
  
    boolean login;
    String NickName;
    public void SecureLogin(){
        login = false;
        NickName = null;
    }
  
    /**Used to bind login to Rsa keys
       @param User: user name
     */
    private void bindName( String User){
        NickName = User;
    }
    
    /** Gets name of user bound to the class instance
     @return String: the user name*/
    public String userBound(){
        return NickName;
    }

    /** Check whether the user has logged successfully
     @return boolean: result of checks*/
    public boolean userLogged(){
        return login;
    }
    
    /**Retrieves a string from a bytes array
       @param arr: array of bytes
       @return String: Converted byte
       @throws UnsupportedEncodingException*/
    private String getText (byte[] arr) throws UnsupportedEncodingException
    {
        String s = new String( arr, "UTF-8" );
        return s;
    }

    /**Computes Salt
       @return byte[]: salt bytes*/
    private byte[] getSalt(){
        Random r = new SecureRandom();
        byte[] salt = new byte[20];
        r.nextBytes(salt);
        return salt;
    }

    /**Computes hash from a string, using SHA1
     @param password: password whereby has calculated
     @param salt: salt bytes
     @return byte[]: Hash code
     @throws NoSuchAlgorithmException
     @throws UnsupportedEncodingException*/
    private byte[] getHash(String password, byte[] salt) throws NoSuchAlgorithmException,UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        digest.reset();
        digest.update(salt);
        byte[] input = digest.digest(password.getBytes("UTF-8"));
        for (int i = 0; i < ITERATION; i++) {
            digest.reset();
            input = digest.digest(input);
        }
        return input;
   }

    /** Checks if a given user name has already taken;
        @param user: user name
        @return boolean true whether the given name is free, false otherwise
        @throws IOException
        @thorws FileNotFoundException
    */
    private boolean CheckUserName(String UserName) throws IOException,FileNotFoundException{
        int ln,ls,lh;
        Directory.MakeDirectory(Directory.CREDENTIALSPATH);
        try{
            FileInputStream fis = new FileInputStream(new File(Directory.CREDENTIALSPATH + FILENAME));
            fis.close();
        }catch(FileNotFoundException x){return true;}
        
        FileInputStream fis = new FileInputStream(new File(Directory.CREDENTIALSPATH + FILENAME));
        
        try{
            while(true){                 
                /*Reads length values*/
                ln = (int) fis.read();
                fis.skip(1);
                ls = (int) fis.read();
                fis.skip(1);
                lh = (int) fis.read();
                fis.skip(1);               
                //Reads user name
                byte[] apn = new byte[ln];
                fis.read(apn);                
                //Checks user name
                if(UserName.compareTo(getText(apn))!=0){
                    fis.skip((int)(ls+lh+1));
                    continue; }               
                else return false;                                
                }                
        }
        catch(Exception x){fis.close();return true;}
    
    }
    /** Puts a new user in the users list
     @param UserName: user name
     @param password: password
     @return boolean: true whether everything has gone well, false otherwise
     @throws IOException
     @throws FileNotFoundException
     @throws NoSuchAlgorithmException
     @throws UnsupportedEncodingException*/
    public boolean newUser(String UserName, String password) throws IOException,FileNotFoundException,NoSuchAlgorithmException,UnsupportedEncodingException{
        
        if(!CheckUserName(UserName))return false;        
        byte[] salt = getSalt();       
        byte[] hash = getHash(password,salt);
        FileOutputStream fos = new FileOutputStream(Directory.CREDENTIALSPATH + FILENAME,true);                    

        //save file
         int ls,lh,ln;

        ln = UserName.length();
        lh = hash.length;
        ls = salt.length;
        
        fos.write((int)ln);
        fos.write('\n');
        fos.write((int)ls);
        fos.write('\n');
        fos.write((int)lh);
        fos.write('\n');
        
        
        fos.write(UserName.getBytes());
        fos.write(salt);
        fos.write(hash);
        fos.write('\n');
        fos.close();          

        bindName(UserName);
        return true;    
    }
    
    
    /**Checks if user credentials are valids
     @param user: user name
     @param password: password
     @return boolean: true whether the credentials given are correct, false otherwise
     @throws IOException
     @throws FileNotFoundException*/
    public boolean LoadUser(String UserName, String password)  throws IOException,FileNotFoundException{
        int ln,ls,lh;
        Directory.MakeDirectory(Directory.CREDENTIALSPATH);
        try{
            FileInputStream fis = new FileInputStream(new File(Directory.CREDENTIALSPATH + FILENAME));
            fis.close();
        }catch(FileNotFoundException x){return false;}
        
        FileInputStream fis = new FileInputStream(new File(Directory.CREDENTIALSPATH + FILENAME));
        
        try{
             while(true){                 
                /*Reads length values*/
                ln = (int) fis.read();
                fis.skip(1);
                ls = (int) fis.read();
                fis.skip(1);
                lh = (int) fis.read();
                fis.skip(1);
                //Reads user name
                byte[] apn = new byte[ln];
                fis.read(apn);
                
                //Checks user name
                if(UserName.compareTo(getText(apn))!=0){
                    fis.skip((int)(ls+lh+1));
                    continue;
                }    
           
                else{
                    byte[] aps = new byte[ls]; //gets salt
                    fis.read(aps);
                    byte[] aph = new byte[lh]; //gets hash
                    fis.read(aph);
                    byte[] chpwd = getHash(password,aps); //compute hash                                       
                    if((getText(aph)).compareTo( getText(chpwd))==0) { login=true; bindName(UserName); return true; }
                    else return false;                                        
                }
                
             }
             
        }
        catch(Exception x){fis.close();return false;}
        
    }
             
}
