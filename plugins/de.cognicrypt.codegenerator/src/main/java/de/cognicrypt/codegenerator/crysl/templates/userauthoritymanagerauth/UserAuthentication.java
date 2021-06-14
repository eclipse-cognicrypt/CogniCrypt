/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/
package de.cognicrypt.codegenerator.crysl.templates.userauthoritymanagerauth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class UserAuthentication verifies the users input, the username and password.
 */
public class UserAuthentication {
	//hash and salt will be stored as string
	/**
	 * Takes username and password as inputs to verifies the password. Connects to the MySQL database
	 * and retrieves the hashed password and salt for the desired username and makes a new hash from the input password
	 * with the same salt, then checks the equality of the hash in the database and the new hash. If the username does not 
	 * exist in the database, it throws an exception. Users may modify the database's username, password, URL and tableName to their own.
	 * Usernames and Passwords are all considered to be unique.
	 * note: SQL must be installed.
	 *
	 * @param username the input username to be searched in the database.
	 * @param pwd the input password for that username, the correction of this password will be checked by this method.
	 * @return true, if the username exists and the input password is correct.
	 * @throws Exception This exception is thrown if the username does not exist in the database.
	 * @throws ClassNotFoundException This exception is thrown if MySQL jar file is not in the buildpath. It contains the class "com.mysql.cj.jdbc.Driver".
	 * @throws GeneralSecurityException  This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	//for this to work, sql must be installed and put in env path
	public static boolean userAuth(String username, char[] pwd) throws Exception {
	    String databaseUsername = "root";
	    String databasePassword = "test";
	    String databaseURL = "jdbc:mysql://localhost:3306/myDatabase";
	    String tableName = " mytb ";

	    Boolean result = false;

	    String salt = "" ;
	    String hash = "";

	    int iterationCount = 65536;
	    int keysize = 128;

	    byte[] hashedPwd = null;

		Class.forName("com.mysql.cj.jdbc.Driver");

		Connection connection = DriverManager.getConnection(databaseURL, databaseUsername, databasePassword);  

	    Statement stmt = connection.createStatement();
	    String SQL = "SELECT * FROM" + tableName + "WHERE USERNAME='" + username + "'";
	    ResultSet queryResult = stmt.executeQuery(SQL);
	    if (queryResult.isBeforeFirst()) {
		    while (queryResult.next()) {
		    	salt = queryResult.getString("salt");
		    	hash = queryResult.getString("hash");
		    }
	    }else {
	    	throw new Exception("User not found");
	    }

	    byte [] bytedSalt = Base64.getDecoder().decode(salt);

		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.PBEKeySpec")
		.addParameter(pwd, "password").addParameter(keysize, "keylength").addParameter(iterationCount, "iterationCount")
		.addParameter(bytedSalt, "salt").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
		.addParameter(hashedPwd, "this").generate();

		String hashedPwdString = Base64.getEncoder().encodeToString(hashedPwd);

	    if (hash.equals(hashedPwdString)) {
	        result = true;}
	    return result;
	}
}