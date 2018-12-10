
package Crypto; 

import java.security.InvalidAlgorithmParameterException;

import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;

import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;

import javax.crypto.IllegalBlockSizeException;

import javax.crypto.NoSuchPaddingException;

import java.security.SecureRandom;

import javax.crypto.spec.IvParameterSpec;

import javax.crypto.spec.SecretKeySpec;

import java.security.spec.InvalidKeySpecException;

import java.util.List;

import java.util.Base64;

import java.io.InputStream;

import java.io.OutputStream;

import java.util.Properties;

import java.io.FileOutputStream;

import java.security.Key;

import java.security.Key;

/** @author CogniCrypt */
public class Enc {	
		
		public byte[] encrypt(byte[] data, Key key) throws GeneralSecurityException { 
			
		byte [] ivb = new byte [16];
	    SecureRandom.getInstanceStrong().nextBytes(ivb);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		
		Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
		c.init(Cipher.ENCRYPT_MODE, key, iv);
		
		byte[] res = c.doFinal(data);
		
		byte [] ret = new byte[res.length + ivb.length];
		System.arraycopy(ivb, 0, ret, 0, ivb.length);
		System.arraycopy(res, 0, ret, ivb.length, res.length);
		
		return ret;		
		
	}
	
	
		public byte[] decrypt(byte [] ciphertext, Key key) throws GeneralSecurityException { 
		
		byte [] ivb = new byte [16];
		System.arraycopy(ciphertext, 0, ivb, 0, ivb.length);
	    IvParameterSpec iv = new IvParameterSpec(ivb);
		byte[] data = new byte[ciphertext.length - ivb.length];
		System.arraycopy(ciphertext, ivb.length, data, 0, data.length);
		
		Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
		c.init(Cipher.DECRYPT_MODE, key, iv);
	
		byte[] res = c.doFinal(data);
		
		return res;		
		
	}
}
