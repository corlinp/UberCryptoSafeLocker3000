package CeC;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils {

	private static final byte[] salt = new byte[]{4,8,15,16,23,42,69,69};
	private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
 
    public static void encrypt(char[] key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
 
    public static void decrypt(char[] key, File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    private static SecretKey keyFromPass(char[] pass, int hashLevel) throws Exception{
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    	KeySpec spec = new PBEKeySpec(pass, salt, hashLevel, 128);
    	SecretKey tmp = factory.generateSecret(spec);
    	SecretKey secret = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    	return secret;
    }
    
    /**
     * We do a different number of iterations to find the hash compared to the encryption key,
     * This way both should be equally and separately hard to break.
     */
    public static byte[] hashOf(char[] pass){
    	try {
			return keyFromPass(pass, 5301).getEncoded();
		} catch (Exception e) {}
    	return null;
    }
 
    private static SecretKey sk = null;
    private static void doCrypto(int cipherMode, char[] key, File inputFile, File outputFile) throws Exception {
        try {
        	if(sk == null)
        		sk = keyFromPass(key, 5120);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, sk);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
             
            inputStream.close();
            outputStream.close();
        } catch (Exception ex) {}
    }

}
