/**
 *  User.java
 *
 *  @author Nilo Redini
 *  @author Davide Pellegrino
 *
 *  This classe allows to manage users' account
 *  After that an user has logged in.
*/

package SecureChat.login;

import SecureChat.crypto.*;
import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;

public class User{
    /*This class must be called after the login using SecureLogin class.
      Indeed the main constructor uses a SecureLogin object to identify an user.
    */
    
    /** User Name*/
    private String UserName;
    /** User whereby the main user has connected*/
    private String FriendName;
    /** Server Ip */
    private String serverIp;
    /** Client ip */
    private String clientIp;
    /** Server port*/
    private int serverPort;
    /** Client port */
    private int clientPort;
    /** Used to manage a secure login */
    private SecureLogin login;
    /** Used to sign data */
    private Rsa rsa;
    /** Used to exchange encrypted messages*/
    private Des des;
    /** Used to check istance validity */
    private boolean valid;
    /** Used to handle DH algorithm*/
    private DiffieHellman dh;

    /**
       Main constructor
       @param port : port whereby the user is connecting
       @param server : Server Ip
       @param log : A secure login istance
     */
    public User(int port,String server,SecureLogin log){
        valid = false;
        this.login = log;
        this.UserName = log.userBound();
        this.serverPort = port;
        this.serverIp = server;
        this.des = null;
        if(this.UserName!=null) valid = true;  
    }
   
    /**
       Checks wheter the class istance is valid or not
       @return boolean: validity
     */
    public boolean isValid(){
        return valid;
    }
    
    /**
       Sets the client port
       @param port : port
     */
    public void setClientPort(int port){
        clientPort = port;
    }

    /**
       Sets the client ip
       @param ip : ip
    */    
    public void setClientIp(String ip){
        clientIp = ip;
    }

    /**
       Sets the name of user whereby the 
       main user is talking
       @param name : Friend name
    */    
    public void setFriendName(String name){
        FriendName = name;
    }

    /**
       sets the server port
       @param port : port
    */
    public void setServerPort(int port){
        this.serverPort = port;
    }
   
    /**
       sets the server ip
       @param server : ip
    */
    public void setServerIp(String server){
        this.serverIp = server;
    }

    /**
       Retrieves name of main user
       @return String: User name
     */
    public String getUserName(){
        return UserName;
    }

    /**
       Retrieves name user whereby
       the main user is talking
       @return String: User name
    */
    public String getFriendName(){
        return FriendName;
    }

    
    /**
       Gets the client port
       @return port: port
    */    
    public int getClientPort(){
        return clientPort;
    }

    /**
       Gets the client port
       @return port: port
    */    
    public String getClientIp(){
        return clientIp;
    }
    
    /**
       Gets the server port
       @return port: port
    */    
    public int getServerPort(){
        return serverPort;
    }
   
    public String getServerIp(){
        return this.serverIp;
    } 
    

    /** RSA methods */

    /**
       Allows to create a pair Rsa keys
       @param KeyDir : directory where the keys will be stored
       @return boolean : True whether everything has gone well
       @throws IOException
       @throws NoSuchAlgorithmException       
     */
    public boolean  CreateRsa(String KeyDir) throws IOException,NoSuchAlgorithmException{
        rsa = new Rsa(KeyDir,login);  
        if(!rsa.setUserName(UserName))return false;
        rsa.createKeys();
        return true;
    }

    /**
       Allows to use Diffie-Hellman's protocol
       @param path : Path of the main variables to use
       @param StreamOut : A socket data stream (out)
       @param ois : A socket data stream (in)
       @return SecretKey : Secret key
       @throws IOException
       @throws SignatureException
     */
    public SecretKey createDiffieHellman(String path,ObjectOutputStream StreamOut,ObjectInputStream ois) throws IOException,SignatureException{
        dh = new DiffieHellman (rsa);        
        SecretKey key = dh.genKeystream(path,StreamOut,ois,FriendName);
        if(dh.isValid())return key;
        return null;
    }
    
    /**
       Checks whether Rsa public key is present or not
       @param UserName : Key owner
       @return boolean : True if the key is present
    */
    public boolean isRsaPresent(String UserName){
        return  rsa.isPresent(UserName);
    }

    
    /**DES methods */
    /**
       Allows to create a des istance
       @param key : Secret shared key
       @return boolean : True if everything has gone well
     */
    public boolean desInstance (SecretKey key, String DesPath){
        des = new Des(key,DesPath);
        return true;
    }

    /**
       This function proof the key confirmation property of DH
       @param key : Key created using Diffie-Hellman
       @param oos : A socket stream (output)
       @param ois : A socket stream (input)
       @return boolean : Result of operation (true if all went well)
       @throws IOException
       @throws NoSuchAlgorithmException
       @throws InvalidKeyException
       @throws ClassNotFoundException
       @throws IllegalBlockSizeException
       @throws BadPaddingException
       @throws NoSuchPaddingException
       
     */
    public boolean keyConfirmation(SecretKey key,ObjectOutputStream oos,ObjectInputStream ois) throws IOException,
                                                                                                      NoSuchAlgorithmException,
                                                                                                      InvalidKeyException,
                                                                                                      ClassNotFoundException,
                                                                                                      IllegalBlockSizeException,
                                                                                                      BadPaddingException,
                                                                                                      NoSuchPaddingException{
        int nonce = dh.getOtherNonce();
        if ( nonce == -1 ) {valid = false; return false;}
        if(des == null) return false;
        oos.writeObject(Encrypt(Integer.toString(nonce)));
        
        if(Integer.parseInt(Decrypt((byte[])ois.readObject())) == dh.getMyNonce()){
            valid = true;
            return true;
        }
        return false;

    }

    /**
       Allows to decrypt a message according des algorithm
       @param data : Data  to decrypt
       @return String : Data decrypted
       @throws NoSuchAlgorithmException
       @throws NoSuchPaddingException,
       @throws InvalidKeyException,
       @throws IllegalBlockSizeException,
       @throws BadPaddingException
     */
    public String Decrypt(byte[] data) throws NoSuchAlgorithmException,
                                              NoSuchPaddingException,
                                              InvalidKeyException,
                                              IllegalBlockSizeException,
                                              BadPaddingException{
        return des.DesDecrypt(data);
    }

    /**
       Allows to encrypt a message according des algorithm
       @param data : Data to encrypt
       @return byte[] : Data encypted
       @throws NoSuchAlgorithmException
       @throws NoSuchPaddingException,
       @throws InvalidKeyException,
       @throws IllegalBlockSizeException,
       @throws BadPaddingException
     */    
    public byte[] Encrypt(String data) throws NoSuchAlgorithmException,
                                              NoSuchPaddingException,
                                              InvalidKeyException,
                                              IllegalBlockSizeException,
                                              BadPaddingException{        
        return des.DesEncrypt(data);
    }
	
}
