package de.cognicrypt.codegenerator.wizard;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class testh {

	public static void main(String args[]){
		String[] allowedModes="CFB|CTR".split("\\|");
		System.out.println(allowedModes[0]);
		 if (!Arrays.asList(allowedModes).contains("CTR"))
		  System.out.println("Pas ici"+allowedModes[0]);
		 }
	}

