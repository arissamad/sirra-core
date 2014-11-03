package com.sirra.server.util;

import java.math.*;

import javax.crypto.*;

/**
 * Use this class to generate a new secret key.
 * 
 * @author aris
 */
public class GenerateSecretKey {
	
	protected static String algorithm = "AES";
	
	public static void main(String[] args) {
		algorithm = "DESede";
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
		    keyGenerator.init(168);
		    SecretKey key = keyGenerator.generateKey();
		    
		    byte[] encodedKey = key.getEncoded();
		    
		    String str = new BigInteger(1, encodedKey).toString(16);
		    System.out.println("Here is your new secret key:\n" + str);
		    System.out.println("\nYou should now save it in the environment variable CONFIG_KEY.");
		} catch(Exception e) {
			System.out.println("Whoops: " + e);
		}
	}
}