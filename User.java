import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import java.security.NoSuchAlgorithmException;


class User{
    private String UserName;
    private String FriendName;
    private String serverIp;
    private String clientIp;
    private int serverPort;
    private int clientPort;
    private Rsa rsa;

    public User(String name, int port,String server){
        this.UserName = name;
        this.serverPort = port;
        this.serverIp = server;
        rsa = new Rsa();
       
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


    public String getFriendName(){
        return FriendName;
    }
    public int getClientPort(){
        return clientPort;
    }

    public String getClientIp(){
        return clientIp;
    }
    

    public void CreateRsa() throws Exception{
        rsa.setUserName(UserName);
        rsa.createKeys();
    }
    
    public boolean isRsaPresent(String UserName){
        return  rsa.isPresent(UserName);
    }
    public String Decrypt(byte[] data) throws Exception{
        return rsa.Decrypt(data);
    }

    public byte[] Encrypt(String data) throws Exception{        
        return rsa.Encrypt(data,rsa.GetPublicKey(FriendName));
    }

   
    public String getUserName(){
        return UserName;
    }

    
    public int getServerPort(){
        return serverPort;
    }
   
    public String getServerIp(){
        return this.serverIp;
    } 
    
}