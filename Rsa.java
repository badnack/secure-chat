/** 
    This class allows to handle RSA keys.
 */

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
public class Rsa {
    public enum KEY {PRIVATE,PUBLIC};
    static final String REQPRIVATEKEY = "null";
    static final String PUBLICPATH = "public.key";
    static final String PRIVATEPATH = "private.key";
    private String KEYPATH; 	
    private String RegUserName;

    public Rsa(String KeyDirectory){
        RegUserName = null;
        KEYPATH =  KeyDirectory;
    }

    public void setKeyDirectory(String KeyDirectory){
            KEYPATH =  KeyDirectory;
    }
    
    /**Set the keys owner
       this function should be called after a login to protect the key from others*/
    public void setUserName(String name){
        this.RegUserName = name;
    }

    /**This method retrieves the keys path by user name*/
    private String UserToPath(String UserName,KEY k){
        if(k==KEY.PUBLIC)
            return KEYPATH + RegUserName + "/" + UserName +"_" + PUBLICPATH;
        
        /*For the private key i can return just the own*/
        return KEYPATH + RegUserName  + "/" + RegUserName + "_" + PRIVATEPATH;
    }

    /** Checks whether a public key is present */
    public boolean isPresent(String UserName){
        try{
            FileInputStream fis = new FileInputStream(KEYPATH + RegUserName + UserName + "_" + PUBLICPATH);
            fis.close();
        }catch(Exception x){return false; }
        return true;
    }

    //Get a public key stored giving the username
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


    public byte[] Encrypt(byte[] data, PublicKey publicKey) throws IllegalBlockSizeException,
                                                                   InvalidKeyException,
                                                                   NoSuchAlgorithmException,
                                                                   BadPaddingException,
                                                                   NoSuchPaddingException {       
        byte[] plainFile=data;
        //plainFile=data.getBytes();               
        // Inizializzo un cifrario che usa come algoritmo RSA, come modalita' ECB e come padding PKCS1
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");        
        // Lo inizializzo dicendo modalita' di codifica e chiave pubblica da usare
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        // codifico e metto il risultato in encodeFile
        byte[] encodeData = c.doFinal(plainFile);
        return encodeData;
    }
    
    public byte[] Decrypt(byte[] sorg) throws IOException, 
                                              InvalidKeyException,
                                              NoSuchAlgorithmException,
                                              InvalidKeySpecException,
                                              NoSuchPaddingException,
                                              IllegalBlockSizeException,
                                              BadPaddingException{        
        // DECODIFICA
        PrivateKey privateKey = GetPrivateKey();
        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        c.init(Cipher.DECRYPT_MODE, privateKey);        
        byte[] plainFile = c.doFinal(sorg);        
        /*// DA BYTE[] A STRING
        StringBuilder sb = new StringBuilder (plainFile.length);
        for (byte b: plainFile)
            sb.append ((char) b);        
        return sb.toString();*/
        return plainFile;
    }
    
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
  
}



