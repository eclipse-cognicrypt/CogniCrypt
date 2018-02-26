package de.cognicrypt.codegenerator.primitive.clafer;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import com.example.java.ceasar.CaesarKey;
import com.example.java.ceasar.CaesarProvider;
public class TestFile{
public static void main(String[] args) {
	  // adding provider dynamically
	  Security.addProvider(new CaesarProvider());

	  try {
	   String message = "Hello World";
	   Cipher c = Cipher.getInstance("Caesar");
	   SecretKey key = new CaesarKey((byte) 2);
	   System.out.println("Message:   " + message);

	   c.init(Cipher.ENCRYPT_MODE, key);
	   byte[] encrypted = c.doFinal(message.getBytes());
	   System.out.println("Encrypted: " + new String(encrypted));

	   c.init(Cipher.DECRYPT_MODE, key);
	   byte[] decrypted = c.doFinal(encrypted);
	   System.out.println("Decrypted: " + new String(decrypted));

	  } catch (Exception e) {
	   e.printStackTrace();
	  }
	 }}