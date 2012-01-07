/**
   This classe allows to manage users' account
   After that an user has logged in.
*/

package SecureChat.login;
import SecureChat.crypto.*;
import javax.crypto.SecretKey;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.security.SignatureException;

public class User{
    private String UserName;
    private String FriendName;
    private String serverIp;
    private String clientIp;
    private int serverPort;
    private int clientPort;
    private SecureLogin login;
    private Rsa rsa;
    private Des des;
    private boolean valid;
    private DiffieHellman dh;

    /*This class must be called after the login using SecureLogin class.
     Indeed the main constructor uses a SecureLogin object to identify an user.
    */
    public User(int port,String server,SecureLogin log){
        valid = false;
        this.login = log;
        this.UserName = log.userBound();
        this.serverPort = port;
        this.serverIp = server;
        if(this.UserName!=null) valid = true;  
    }
   
    public boolean isValid(){
        return valid;
    }
    
    public void setClientPort(int port){
        clientPort = port;
    }

    public void setClientIp(String ip){
        clientIp = ip;
    }

    public void setFriendName(String name){
        FriendName = name;
    }

    public void setServerPort(int port){
        this.serverPort = port;
    }
   
    public void setServerIp(String server){
        this.serverIp = server;
    }

    public String getUserName(){
        return UserName;
    }

    public String getFriendName(){
        return FriendName;
    }
    public int getClientPort(){
        return clientPort;
    }

    public String getClientIp(){
        return clientIp;
    }
    
    public int getServerPort(){
        return serverPort;
    }
   
    public String getServerIp(){
        return this.serverIp;
    } 
    

    /**RSA methods*/
    public boolean  CreateRsa(String KeyDir) throws Exception{
        rsa = new Rsa(KeyDir,login);  
        if(!rsa.setUserName(UserName))return false;
        rsa.createKeys();
        return true;
    }

    public SecretKey createDiffieHellman(String path,ObjectOutputStream StreamOut,ObjectInputStream ois) throws IOException,SignatureException{
        dh = new DiffieHellman (rsa);
        return dh.genKeystream(path,StreamOut,ois,FriendName);
        
    }
    
    public boolean isRsaPresent(String UserName){
        return  rsa.isPresent(UserName);
    }

    public boolean SignMessage(){
        return true;
    }
    
    public boolean CheckSign(){ return true;}


    /**DES methods */
    public boolean desInstance (SecretKey key){
        des = new Des(key);
        return true;
    }    

    public String Decrypt(byte[] data) throws Exception{
        return des.DesDecrypt(data);
    }
    
    public byte[] Encrypt(String data) throws Exception{        
        return des.DesEncrypt(data);
    }
	
    /*public String Decrypt(byte[] data) throws Exception{
        return rsa.Decrypt(data);
    }

    public byte[] Encrypt(String data) throws Exception{        
        return rsa.Encrypt(data,rsa.GetPublicKey(FriendName));
    }*/
}
