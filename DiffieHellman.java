import java.math.BigInteger;
import java.security.spec.InvalidKeySpecException;
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
import java.util.Random;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

class DiffieHellman{
    static final int LENGTH_RANDOM_EXPONENT = 200;
    static final String SIGSEPARATOR = "DIGSIG";
   
    String[] values; //prendo p e g da un file
    private BigInteger p; 
    private BigInteger g; 
    private int len;  
    byte[] publicKeyBytes ;
    boolean valid;
    Rsa rsa;

    public  DiffieHellman(Rsa rsa){
        len = LENGTH_RANDOM_EXPONENT;
        publicKeyBytes = null;
        valid = false;
        p = null;
        g = null;
        this.rsa = rsa;
    }

    public boolean isValid(){
        return valid;
    }
    
    private String getText (byte[] arr) throws UnsupportedEncodingException
    {
        String s = new String( arr, "UTF-8" );
        return s;
    }

    public void readBaseKey(String PathBase) throws IOException{
        BufferedReader fis = new BufferedReader(new FileReader(PathBase));
        p = new BigInteger(fis.readLine()); //almeno 300 cifre
        g = new BigInteger(fis.readLine());
        fis.close();
    }

    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }


    private int byteArrayToInt(byte [] b) {
        return (b[0] << 24)
            + ((b[1] & 0xFF) << 16)
            + ((b[2] & 0xFF) << 8)
            + (b[3] & 0xFF);
    }

  
    private byte[] concatBytes(byte[] A, byte[] B) {
        byte[] C= new byte[A.length+B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        
        return C;
    }
    

    private boolean CheckFreshness(ObjectOutputStream StreamOut,ObjectInputStream StreamIn,String FName)throws IOException,
                                                                                                               ClassNotFoundException,
                                                                                                               InvalidKeyException, 
                                                                                                               SignatureException,
                                                                                                               UnsupportedEncodingException,
                                                                                                               InvalidKeySpecException,
                                                                                                               NoSuchAlgorithmException {
        Random rnd = new Random();
        int na = rnd.nextInt(1000);
       
       //signs the nonces
        byte [] signed = rsa.SignMessage(Integer.toString(na));
        
        
        StreamOut.writeObject(Integer.toString(na).getBytes());         
        StreamOut.writeObject(signed);         
        
        byte[] nonce = (byte[]) StreamIn.readObject();
         signed = (byte[]) StreamIn.readObject();
        
         System.out.println(signed);
        if(!rsa.CheckSign(nonce,signed,FName))
            return false;
        

        int nb = Integer.parseInt(getText(nonce));
       
        nb-=1;
        

        //signs the nonces
        signed = rsa.SignMessage(Integer.toString(nb));
        
        //Sends number
        StreamOut.writeObject(Integer.toString(nb).getBytes());         
        StreamOut.writeObject(signed);         
        System.out.println(signed);
        nonce = (byte[]) StreamIn.readObject();
        signed = (byte[]) StreamIn.readObject();

        if(!rsa.CheckSign(nonce,signed,FName))
            return false;

      
        if( Integer.parseInt(getText(nonce)) != (na-1)){
            //trust exception
            return false;
            }
        return true;
        
    }

        public SecretKey genKeystream(String PathBase,ObjectOutputStream StreamOut,ObjectInputStream StreamIn,String FName) throws IOException, SignatureException{    
        
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

            //nounces
            System.out.println(CheckFreshness(StreamOut,StreamIn,FName));
    
            
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
            String algorithm = "DESede";
        
            // Generate the secret key
            SecretKey secretKey = ka.generateSecret(algorithm);
            valid = true;
            return secretKey;
           
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
