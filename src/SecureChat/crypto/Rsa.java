/**
 *  Rsa.java
 *
 *  @author Nilo Redini
 *  @author Davide Pellegrino
 *
 *  This class allows to handle Rsa keys.
 * 
*/

package SecureChat.crypto;
import SecureChat.login.*;

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
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.InvalidKeyException;

public class Rsa {
    /** Enum */
    public enum KEY {PRIVATE,PUBLIC};
    /** Dummy string to request private key */
    static final String REQPRIVATEKEY = "null";
    /** String used to find public keys */
    static final String PUBLICPATH = "public.key";
    /** String used to find private keys */
    static final String PRIVATEPATH = "private.key";
    /** Keys path */
    private String KEYPATH; 	
    /** Keys owner */
    private String RegUserName;
    /** Used just to make this class secure: this class must be work with SecureLogin */
    SecureLogin login;

    /**
       Main constructor
       @param login : Used to identify the owner of rsa keys
       @param KeyDirectory : Directory of the keys
     */
    public Rsa(String KeyDirectory,SecureLogin login){
        RegUserName = null;
        KEYPATH =  KeyDirectory;
        this.login = login;
    }

    /**
       Sets the keys directory
       @param KeyDirectory : Path so set as default
     */
    public void setKeyDirectory(String KeyDirectory){
            KEYPATH =  KeyDirectory;
    }
    
    /**Set the keys owner
       this function has called after a login in order to protect the key from others on the same computer
       @param name : Name of the user
       @return boolean : True whether the user has already logged
    */
    public boolean setUserName(String name){
        if ( name.compareTo(login.userBound())!=0 )return false;
        if (!login.userLogged())return false;
        this.RegUserName = name;
        return true;
    }

    /**
       This method retrieves the keys path by user name
       @param UserName : Name of the user
       @param k : The kind of key to search
       @return String : Path
     */
    private String UserToPath(String UserName,KEY k){
        if(k==KEY.PUBLIC)
            return KEYPATH + RegUserName + "/" + UserName +"_" + PUBLICPATH;
        
        /*For the private key i can return just the own*/
        return KEYPATH + RegUserName  + "/" + RegUserName + "_" + PRIVATEPATH;
    }

    /** 
        Checks whether a public key is present 
        @param UserName : User name
        @return boolean : True if present, false otherwise
    */
    public boolean isPresent(String UserName){
        try{
            FileInputStream fis = new FileInputStream(UserToPath(UserName,KEY.PUBLIC));
            fis.close();
        }
        catch(Exception x){
            return false; 
        }
        return true;
    }
    
    /**
       Gets a public key stored giving the username
       @param UserName : User name
       @return PublicKey
       @throws IOException
       @throws InvalidKeySpecException
       @throws NoSuchAlgotithmException
    */
    public PublicKey GetPublicKey(String UserName) throws IOException,InvalidKeySpecException,NoSuchAlgorithmException{
        String path = UserToPath (UserName,KEY.PUBLIC);
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = 0;
        while((i = fis.read()) != -1) {
            baos.write(i);
        }
        
        byte[] publicKeyBytes = baos.toByteArray();
        baos.close();
        // Inizializza convertitore da X.509 a chiave pubblica
        X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyBytes);
        // Inizializza un KeyFactory per ricreare la chiave usando RSA 
        KeyFactory kf = KeyFactory.getInstance("RSA");
        // Crea una chiave pubblica usando generatePublic di KeyFactory in base la chiave decodificata da ks
        return kf.generatePublic(ks); 

    }

    /**
       Gets a private key stored of the owner
       @return PrivateKey
       @throws IOException
       @throws InvalidKeySpecException
       @throws NoSuchAlgotithmException
    */
    private PrivateKey GetPrivateKey() throws IOException,InvalidKeySpecException,NoSuchAlgorithmException{
        String path = UserToPath (REQPRIVATEKEY,KEY.PRIVATE);
        
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        int i = 0;
        while((i = fis.read()) != -1) {
            baos.write(i);
        }
        byte[] privateKeyBytes = baos.toByteArray();
        baos.close();                       
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
       return kf.generatePrivate(ks);
       
    }


    /**
       This function allows to create a pair of RSA keys which will be stored
       in two different file
       @throws IOException
       @throws NoSuchAlgorithmException
     */
    public void createKeys() throws IOException,NoSuchAlgorithmException {
        String UserName = RegUserName;
        boolean Exists = true;
        // GENERA COPPIA DI CHIAVI
        try{
            FileInputStream fis = new FileInputStream(KEYPATH + UserName + "/" + UserName + "_" + PUBLICPATH);
        }catch(Exception x){
            //creates a key file to store keys
            Exists = false;
        }
        
        if(Exists) return;
                
        //inizializza un generatore di coppie di chiavi usando RSA
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        // genera la coppia
        KeyPair kp = kpg.generateKeyPair();
        
        // SALVA CHIAVE PUBBLICA        
        byte[] publicBytes = kp.getPublic().getEncoded();
        // salva nel keystore selezionato dall'utente
        FileOutputStream fos = new FileOutputStream( KEYPATH + UserName + "/" + UserName + "_" + PUBLICPATH);
        
        fos.write(publicBytes);
        fos.close();
        
        byte[] privateBytes = kp.getPrivate().getEncoded();        
        fos = new FileOutputStream(KEYPATH + UserName + "/" + UserName + "_" + PRIVATEPATH);
        fos.write(privateBytes);
        fos.close();
    }

    public byte[] SignMessage(byte[] message) throws SignatureException,InvalidKeyException,IOException,NoSuchAlgorithmException,InvalidKeySpecException{
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initSign(GetPrivateKey());
        sig.update(message);
        return sig.sign();
    }

    public boolean CheckSign(byte[] message,byte[] sign, String UserName) throws SignatureException,InvalidKeyException,IOException,NoSuchAlgorithmException,InvalidKeySpecException{
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(GetPublicKey(UserName));
        sig.update(message);
        return sig.verify(sign);
      
    }
}



