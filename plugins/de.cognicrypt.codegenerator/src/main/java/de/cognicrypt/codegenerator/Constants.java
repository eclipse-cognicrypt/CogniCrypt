/**
 * Copyright 2015-2017 Technische Universitaet Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @author Stefan Krueger
 *
 */

package de.cognicrypt.codegenerator;

/**
 * This class comprises all constants that are used by the plugin.
 *
 */
public class Constants {

	public static enum GUIElements {
		combo, text, itemselection,button
	}

	//The plugin is bundled in a jar archive and the file separator within jar files is / (see: https://stackoverflow.com/questions/24749007/how-to-use-file-separator-for-a-jar-file-resource).
	//Use this file separator for all paths within the plugin space.
	public static final String innerFileSeparator = "/";

	//Use this file separator for all paths outside the plugin space.
	public static final String outerFileSeparator = System.getProperty("file.separator");;
	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String JavaNatureID = "org.eclipse.jdt.core.javanature";

	private static final String rsrcPath = "src" + innerFileSeparator + "main" + innerFileSeparator + "resources" + innerFileSeparator;
	public static final String providerPath = rsrcPath + "AdditionalResources" + innerFileSeparator + "Provider";
	
	
	//JSON task file
	public static final String jsonTaskFile = rsrcPath + "Tasks" + innerFileSeparator + "tasks.json";;

	public static final String pathToPropertyfiles = rsrcPath + "Labels.properties";

	
	// Task descriptions

	// if the next question page depends on user input, the Page object encodes this as a nextPageID as opposed to the one that the last page points to
	public static final int QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID = -2;

	// the last page points to this virtual nextPageID
	public static final int QUESTION_PAGE_LAST_PAGE_NEXT_ID = -1;

	// the answer does not point to a next page, so in this case the page links to a next one statically
	public static final int ANSWER_NO_NEXT_ID = -2;
	
	// the given answer makes the wizard end
	public static final int ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID = -1;

	// Clafer Instance Generation
	public static final String claferPath = rsrcPath + "ClaferModel" + innerFileSeparator + "SymmetricEncryptionTask.js";
	public static final String PATH_FOR_CONFIG_XML = "/Configurator.xml";
	public static final String XML_FILE_NAME = rsrcPath + "ClaferModel/Encrypt_CryptoTasks.xml";

	// Input for Code Generation
	public static final String pathToXSLFile = rsrcPath + "XSLTemplates" + innerFileSeparator + "JCA.xsl";
	public static final String pathToClaferInstanceFolder = rsrcPath + "ClaferInstance" + innerFileSeparator;
	public static final String pathToClaferInstanceFile = "claferInstance.xml";
	public static final String NameOfTemporaryMethod = "templateUsage";
	public static final String pathsForLibrariesinDevProject = "libs";

	// Output of Code Generation
	private static final String AdditionalOutputFile = "Output.java";
	public static final String PackageName = "Crypto";
	public static final String CodeGenerationCallFile = innerFileSeparator + Constants.PackageName + innerFileSeparator + AdditionalOutputFile;

	// Error Messages
	public static final String NoFileOpenedErrorMessage = "There is no file opened to generate the source code in. Will generate output file instead.";
	public static final String NoJavaFileOpenedErrorMessage = "The currently open file is not a java file. Will generate output file instead.";
	public static final String CodeGenerationErrorMessage = "An error occured during template generation.";
	public static final String FilesDoNotExistErrorMessage = "At least one of the files necessary for template generation does not exist.";
	public static final String NoRunMethodFoundInTemporaryOutputFileErrorMessage = "XSL Template does not contain method " + NameOfTemporaryMethod + ".";
	public static final String NoTemporaryOutputFile = "Temporary output file does not exist.";
	public static final String NoFileandNoProjectOpened = "No file opened and no project selected.";
	public static final String NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE = "No possible combinations are available for chosen values. Please modify your preferences and try agin.\n \n You can use  \n>= insted of >\n<= instead of <\nto make your selection generic.";
	public static final String NO_POSSIBLE_COMBINATIONS_BEGINNER = "No possible combinations are available for chosen values. Please modify your preferences and try agin.";
	public static final String PLEASE_SELECT = "Please select project directory to launch the configurator";
	public static final String ERROR_MESSAGE_NO_PROJECT = "There is no Java project in your workspace. Please create one and restart CogniCrypt.";
	public static final String ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY = "No directory for additional resources found.";

	public static final String JAVA = "java";
	public static final String JCA = "JCA";
	public static final String JAR = ".jar";
	public static final String ALGORITHM = "algorithm";
	public static final String ADVANCED_MODE = "Advanced Mode";

	public static final String Package = "Package";
	public static final String Description = "description";
	public static final String Imports = "Imports";
	public static final String Import = "Import";
	public static final String Task = "task";
	public static final String Code = "code";
	public static final String Type = "type";
	public static final String[] xmlimportsarr = { "java.security.InvalidAlgorithmParameterException", "java.security.InvalidKeyException", "java.security.NoSuchAlgorithmException", "java.security.NoSuchAlgorithmException", "javax.crypto.SecretKey", "javax.crypto.BadPaddingException", "javax.crypto.Cipher", "javax.crypto.IllegalBlockSizeException", "javax.crypto.NoSuchPaddingException", "java.security.SecureRandom", "javax.crypto.spec.IvParameterSpec", "javax.crypto.spec.SecretKeySpec", "java.security.spec.InvalidKeySpecException", "java.util.List;" };

	
	// Types
	public static final String INTEGER = "Integer";
	public static final String STRING = "String";
	public static final String BOOLEAN = "Boolean";

	public static final int INT_HIGH = 600;
	public static final int INT_LOW = -17;
	public static final String INSTANCE_DETAILS = "Instance Details";
	public static final String ARIAL = "Arial";
	public static final String SELECT_JAVA_PROJECT = "Select a Java Project :";
	public static final String SELECT_TASK = "Select Task :";
	public static final String NO_XML_INSTANCE_FILE_TO_WRITE = "No xml instance file to write.";
	public static final String CLAFER_ALGORITHM = "_Algorithm";

	//Flags for default project selection
	public static boolean WizardActionFromContextMenuFlag = false;
	
}

