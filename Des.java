import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/** 
    This class allows to handle TripleDES with Padding keys.
 */
public class Des {
	private SecretKey SessionKey;
	//private String IV ;
	
	/**method for the client*/
	public Des (SecretKey key) {
		this.SessionKey = key;
	}
	
	/**method for the server*/
	public Des () throws NoSuchAlgorithmException{
		KeyGenerator DESedeKeyGen = KeyGenerator.getInstance("DESede");
	    SecretKey DESedeKey = DESedeKeyGen.generateKey();
		this.SessionKey = DESedeKey;
	    }
	
	
	
	public SecretKey getSessionKey () {
		return this.SessionKey;
	}
	
	public byte[] DesEncrypt (String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{ 
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
	
	public String DesDecrypt (byte[] EncryptData) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
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
