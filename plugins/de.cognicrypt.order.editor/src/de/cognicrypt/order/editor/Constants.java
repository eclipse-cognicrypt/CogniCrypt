/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.order.editor;

/**
 * This class comprises all constants that are used by the plugin.
 */
public class Constants {

	public static final String innerFileSeparator = "/";
	//public static final String rsrcPath = "src" + innerFileSeparator + "main" + innerFileSeparator + "resources" + innerFileSeparator;
	
	public final static String RELATIVE_STATEMACHINE_CONFIG_DIR = "config" + innerFileSeparator;
	public final static String RELATIVE_STATEMACHINE_MODELS_DIR = "output" + innerFileSeparator;
	
	public static final String STATEMACHINE_EXTENSION = ".statemachine";
	public static final String XML_EXTENSION = ".xml";
	
	public final static String RELATIVE_JCA_FOLDER = innerFileSeparator + "git" + innerFileSeparator + "Crypto-API-Rules" + innerFileSeparator + "JavaCryptographicArchitecture" + innerFileSeparator + "src";
	public final static String RELATIVE_BC_FOLDER = innerFileSeparator + "git" + innerFileSeparator + "Crypto-API-Rules" + innerFileSeparator + "BouncyCastle" + innerFileSeparator + "src";
	public final static String RELATIVE_BC_JCA_FOLDER = innerFileSeparator + "git" + innerFileSeparator + "Crypto-API-Rules" + innerFileSeparator + "BouncyCastle-JCA" + innerFileSeparator + "src";
	public final static String RELATIVE_TINK_FOLDER = innerFileSeparator + "git" + innerFileSeparator + "Crypto-API-Rules" + innerFileSeparator + "Tink" + innerFileSeparator + "src";
	
	public static final String ERROR_MESSAGE_NO_FILE = "No file found";
}
