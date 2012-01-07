/**
 *  Des.java
 *
 *  @author Nilo Redini
 *  @author Davide Pellegrino
 *
 *  This class allows to handle TripleDES with Padding keys.
 * 
*/


package SecureChat.crypto;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Des {
    
    /** Private session key*/
    private SecretKey SessionKey;
	
    /** 
        Main Constructor
        @param key : A secret shared key
     */
    public Des (SecretKey key) {
        this.SessionKey = key;
    }
	

    /**
       Get the shared key
       @return SecretKey
     */
    public SecretKey getSessionKey () {
        return this.SessionKey;
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
    public byte[] DesEncrypt (String data) throws NoSuchAlgorithmException,
                                                  NoSuchPaddingException, 
                                                  InvalidKeyException, 
                                                  IllegalBlockSizeException, 
                                                  BadPaddingException{ 
        //Create the Cipher
        Cipher DESedeCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        
        // Initialize the cipher for encryption
        DESedeCipher.init(Cipher.ENCRYPT_MODE, this.SessionKey);
        
        // From String to byte[]
        byte[] ClearData = data.getBytes();
        
        // Encrypt the ClearData
        byte[] EncryptData = DESedeCipher.doFinal(ClearData);
        
        return EncryptData;
    }
    

    /**
       Allows to decrypt a message according des algorithm
       @param EncryptData : Data  to decrypt
       @return String : Data decrypted
       @throws NoSuchAlgorithmException
       @throws NoSuchPaddingException,
       @throws InvalidKeyException,
       @throws IllegalBlockSizeException,
       @throws BadPaddingException
     */
    public String DesDecrypt (byte[] EncryptData) throws NoSuchAlgorithmException, 
                                                         NoSuchPaddingException, 
                                                         InvalidKeyException, 
                                                         IllegalBlockSizeException, 
                                                         BadPaddingException {
        //Create the Cipher
        Cipher DESedeCipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        
        // Initialize the cipher for decryption
        DESedeCipher.init(Cipher.DECRYPT_MODE, this.SessionKey);
        
        // Decrypt the EncryptData
        byte[] ClearData = DESedeCipher.doFinal(EncryptData);
        
        //From byte[] to String
        StringBuilder sb = new StringBuilder (ClearData.length);
        for (byte b: ClearData)
            sb.append ((char) b);        
        return sb.toString();
        
    }
}
