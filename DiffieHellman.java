import java.math.BigInteger;
import java.io.IOException;
import javax.crypto.SecretKey;
import javax.crypto.KeyAgreement;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.spec.DHParameterSpec;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
class DiffieHellman{
    static final int LENGTH_RANDOM_EXPONENT = 300;
    
   
    String[] values; //prendo p e g da un file
    private BigInteger p; 
    private BigInteger g; 
    private int len;  
    byte[] publicKeyBytes ;
    boolean valid;

    public void DiffieHellman(){
        len = LENGTH_RANDOM_EXPONENT;
        publicKeyBytes = null;
        valid = false;
        p = null;
        g = null;
    }

    public boolean isValid(){
        return valid;
    }
    
    public void readBaseKey(String PathBase) throws IOException{
        BufferedReader fis = new BufferedReader(new FileReader(PathBase));
        p = new BigInteger(fis.readLine()); //almeno 300 cifre
        g = new BigInteger(fis.readLine());
        fis.close();
    }

    public SecretKey genKeystream(String PathBase,ObjectOutputStream StreamOut,ObjectInputStream StreamIn) throws IOException{    
        try {
            
            readBaseKey(PathBase);
            
            // Use the values to generate a key pair
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            DHParameterSpec dhSpec = new DHParameterSpec(p, g, len);
            keyGen.initialize(dhSpec);
            KeyPair keypair = keyGen.generateKeyPair();
            
            // Get the generated public and private keys
            PrivateKey privateKey = keypair.getPrivate();
            PublicKey publicKey = keypair.getPublic();
        
            // Send the public key bytes to the other party...
            StreamOut.writeObject(publicKey.getEncoded());

            // Retrieve the public key bytes of the other party
            //publicKeyBytes = ...;
            publicKeyBytes = (byte[])StreamIn.readObject();
            
            // Convert the public key bytes into a PublicKey object
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFact = KeyFactory.getInstance("DH");
            publicKey = keyFact.generatePublic(x509KeySpec);
            
            // Prepare to generate the secret key with the private key and public key of the other party
            KeyAgreement ka = KeyAgreement.getInstance("DH");
            ka.init(privateKey);
            ka.doPhase(publicKey, true);
            
            // Specify the type of key to generate;
            // see Listing All Available Symmetric Key Generators
            String algorithm = "DES";
        
            // Generate the secret key
            SecretKey secretKey = ka.generateSecret(algorithm);
            valid = true;
            return secretKey;
            // Use the secret key to encrypt/decrypt data;
            // see Encrypting a String with DES
        } 
        catch (java.security.InvalidKeyException e) {
        } catch (java.security.spec.InvalidKeySpecException e) {
        } catch (java.security.InvalidAlgorithmParameterException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(ClassNotFoundException x){
        } catch (IOException e) {
            valid = false;
            throw e;
        }
        
        return null;
         
    }
}
