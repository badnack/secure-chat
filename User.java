import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
   This classe allows to manage users' account
*/

class User{
    private String UserName;
    private String FriendName;
    private String serverIp;
    private String clientIp;
    private int serverPort;
    private int clientPort;
    private Rsa rsa;
    private Des des;

    public User(String name, int port,String server,String KeyDir){
        this.UserName = name;
        this.serverPort = port;
        this.serverIp = server;
        rsa = new Rsa(KeyDir);  
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
    public void CreateRsa() throws Exception{
        rsa.setUserName(UserName);
        rsa.createKeys();
    }
    
    public boolean isRsaPresent(String UserName){
        return  rsa.isPresent(UserName);
    }
    public byte[] Decrypt(byte[] data) throws Exception{
        return rsa.Decrypt(data);
    }

    public byte[] Encrypt(byte[] data) throws Exception{        
        return rsa.Encrypt(data,rsa.GetPublicKey(FriendName));
    }
    
    /**DES Create method 
     * @throws NoSuchAlgorithmException
     * se accetta il valore nulla per la chiave si può eliminare il booleano */
    public void CreateDes(boolean exist, SecretKey key) throws NoSuchAlgorithmException{
    	if(exist) des = new Des(key); //create session key for the client
    	else des = new Des(); //create session key for the server
    }
    
    /**DES Decrypt method
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException */
    public String DesDecrypt(byte[] EcnryptData) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	return des.DesDecrypt(EcnryptData);
    }
    
    /**DES Encrypt method
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException */
    public byte[] DesEncrypt(String ClearData) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	return des.DesEncrypt(ClearData);
    }
    
    /**Return DES session key method*/
    public SecretKey getDesKey(){
    	return des.getSessionKey();
    }
    
}