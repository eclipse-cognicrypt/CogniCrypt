/**
 * Copyright 2015-2016 Technische Universitaet Darmstadt
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

package crossing.e1.configurator;

/**
 * This class consists of all constants that are used by the plugin.
 *
 */
public class Constants {

	public static enum guiElements {
		combo, text, itemselection
	}

	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String JavaNatureID = "org.eclipse.jdt.core.javanature";
	//JSON task file
	public static final String jsonTaskFile = "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "Tasks" + fileSeparator + "tasks.json";;

	// Clafer Instance Generation
	public static final String claferPath = "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "ClaferModel" + fileSeparator + "SymmetricEncryptionTask.js";
	public static final String PATH_FOR_CONFIG_XML = "/Configurator.xml";
	public static final String XML_FILE_NAME = "src/main/resources/ClaferModel/Encrypt_CryptoTasks.xml";

	// Input for Code Generation
	public static final String pathToXSLFile = "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "XSLTemplates" + fileSeparator + "JCA.xsl";
	public static final String pathToTSLXSLFile = "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "XSLTemplates" + fileSeparator + "TLS.xsl";
	public static final String pathToClaferInstanceFolder = "src" + fileSeparator + "main" + fileSeparator + "resources" + fileSeparator + "ClaferInstance" + fileSeparator;
	public static final String pathToClaferInstanceFile = "claferInstance.xml";
	public static final String pathToClaferInstanceTLSFile = "claferInstanceTLS.xml";
	public static final String NameOfTemporaryMethod = "run";
	public static final String pathsForLibrariesinDevProject = "libs";

	// Output of Code Generation
	public static final String AdditionalOutputFile = "Output.java";
	public static final String PackageName = "Crypto";
	public static final String CodeGenerationCallFile = fileSeparator + Constants.PackageName + fileSeparator + AdditionalOutputFile;

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

	public static final String JAVA = "java";
	public static final String ALGORITHM = "algorithm";
	public static final String ADVANCED_MODE = "Advanced Mode";
	public static final String TEST_CONNECTION = "Test Connection";
	
	public static final String Package = "Package";
	public static final String Description = "description";
	public static final String Imports = "Imports";
	public static final String Import = "Import";
	public static final String Task = "task";
	public static final String Code = "code";
	public static final String Type = "type";
	public static final String[] xmlimportsarr = {"java.security.InvalidAlgorithmParameterException", 
												  "java.security.InvalidKeyException",
												  "java.security.NoSuchAlgorithmException",
												  "java.security.NoSuchAlgorithmException",
												  "javax.crypto.SecretKey",
												  "javax.crypto.BadPaddingException",
												  "javax.crypto.Cipher",
												  "javax.crypto.IllegalBlockSizeException",
												  "javax.crypto.NoSuchPaddingException",
												  "java.security.SecureRandom",
												  "javax.crypto.spec.IvParameterSpec",
												  "javax.crypto.spec.SecretKeySpec",
												  "java.security.spec.InvalidKeySpecException",
												  };

	// Types
	public static final String INTEGER = "Integer";
	public static final String STRING = "String";
	public static final String BOOLEAN = "Boolean";

	public static final int INT_HIGH = 600;
	public static final int INT_LOW = -17;
	public static final int DEFAULT_SCOPE = 5;
	public static final String INSTANCE_DETAILS = "Instance Details";
	public static final String ARIAL = "Arial";
	public static final String SELECT_TASK = "Select Task :";
	public static final String NO_XML_INSTANCE_FILE_TO_WRITE = "No xml instance file to write.";
	public static final String CLAFER_ALGORITHM = "_Algorithm";
}
