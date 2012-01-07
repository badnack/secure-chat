package SecureChat.crypto;
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
import java.util.Arrays; 



//modo migliore per la gestione da int a byte[]!!!
public class DiffieHellman{
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

    private byte[] concatBytes(byte[] A, byte[] B) {
        byte[] C= new byte[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
        }
    
    private int findDelimiterBytes(byte [] b, int off,String sep){
        int len = sep.length();
        int k=0;
        int ap=0;
        for(int i = off ; i < b.length ; i++){
            if((char)b[i]== sep.charAt(0) ){
                ap=len;
                k=0;
                for(int j=i; j<ap+i ;j++){                                        
                    if((char)b[j]!=sep.charAt(k)){len=ap; break;}
                    len--; k++;
                }
                if(len == 0) return i;
            }
        }
        return -1;
    }

    private byte[] CheckFreshness(ObjectOutputStream StreamOut,ObjectInputStream StreamIn,PublicKey publicKey,String FName)throws IOException,
                                                                                                                                   ClassNotFoundException,
                                                                                                                                   InvalidKeyException, 
                                                                                                                                   SignatureException,
                                                                                                                                   UnsupportedEncodingException,
                                                                                                                                   InvalidKeySpecException,
                                                                                                                                   NoSuchAlgorithmException {
        Random rnd = new Random();
        int na = rnd.nextInt(1000);
       
       //signs the nonces
        byte [] signed = rsa.SignMessage((Integer.toString(na)).getBytes());

        String p1 = Integer.toString(na) + SIGSEPARATOR;
        byte[] toSend = concatBytes(p1.getBytes(),signed);
        StreamOut.writeObject(toSend);

        byte[] nonce = (byte[]) StreamIn.readObject();
        
        int del = findDelimiterBytes(nonce,0,SIGSEPARATOR);
        byte[] num = Arrays.copyOfRange(nonce, 0, del);
        byte[] sig = Arrays.copyOfRange(nonce,del+SIGSEPARATOR.length(),nonce.length);
        if(!rsa.CheckSign(num,sig,FName))
            return null;
           
        
        int nb = Integer.parseInt(getText(num));
        nb-=1;
        
        //signs the new nonces and the the part of secret
        p1 = Integer.toString(nb) + SIGSEPARATOR;
        byte[] tosign = concatBytes(p1.getBytes(),publicKey.getEncoded());
        signed = rsa.SignMessage(tosign);
        
        //Sends number
        p1 = Integer.toString(nb) + SIGSEPARATOR;
        byte[] tocn1 = concatBytes(p1.getBytes(),publicKey.getEncoded());
        byte[] tocn2 = concatBytes(tocn1,SIGSEPARATOR.getBytes());
        byte[] toSend2 = concatBytes(tocn2,signed);
        StreamOut.writeObject(toSend2);



        byte[] nonce2 = (byte[]) StreamIn.readObject();
        
        del = findDelimiterBytes(nonce2,0,SIGSEPARATOR);
        del = findDelimiterBytes(nonce2,del+SIGSEPARATOR.length(),SIGSEPARATOR);
      
        byte[] Part1 = Arrays.copyOfRange(nonce2, 0, del);
        byte[] sig2 = Arrays.copyOfRange(nonce2,del+SIGSEPARATOR.length(),nonce2.length);
        if(!rsa.CheckSign(Part1,sig2,FName))
            return null;
      
        del = findDelimiterBytes(Part1,0,SIGSEPARATOR);
        byte[] num2 = Arrays.copyOfRange(Part1, 0, del);
        byte[] key = Arrays.copyOfRange(Part1,del + SIGSEPARATOR.length() ,Part1.length); 
      
        if( Integer.parseInt(getText(num2)) != (na-1)){
            //trust exception
            return null;
            }
        return key;
            
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
            publicKeyBytes = CheckFreshness(StreamOut,StreamIn,publicKey,FName);
            if(publicKeyBytes == null) return null;
              
            
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
