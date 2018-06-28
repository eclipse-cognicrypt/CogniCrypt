package de.cognicrypt.core;

/**
 * This class comprises all constants that are used by the plugin.
 *
 */
public class Constants {

	public enum GUIElements {
		combo, text, itemselection, button, radio, scale
	}

	//The plugin is bundled in a jar archive and the file separator within jar files is / (see: https://stackoverflow.com/questions/24749007/how-to-use-file-separator-for-a-jar-file-resource).
	//Use this file separator for all paths within the plugin space.
	public static final String innerFileSeparator = "/";

	//Use this file separator for all paths outside the plugin space.
	public static final String outerFileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String JavaNatureID = "org.eclipse.jdt.core.javanature";

	private static final String rsrcPath = "src" + Constants.innerFileSeparator + "main" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator;
	public static final String providerPath = Constants.rsrcPath + "AdditionalResources" + Constants.innerFileSeparator + "Provider";

	//JSON task file
	public static final String jsonTaskFile = Constants.rsrcPath + "Tasks" + Constants.innerFileSeparator + "tasks.json";

	public static final String pathToPropertyfiles = Constants.rsrcPath + "Labels.properties";

	// Task descriptions

	// Tooltip
	public static final String PROJECTLIST_TOOLTIP = "List of your Java projects";
	public static final String TASKLIST_TOOLTIP = "Cryptographic tasks supported by CogniCrypt";
	public static final String DESCRIPTION_BOX_TOOLTIP = "Here is the description for the cryptographic task that you have selected";
	public static final String GUIDEDMODE_TOOLTIP = "Guided mode configures the algorithm for you,\nbased on your answers to some simple questions.";
	public static final String DEFAULT_ALGORITHM_COMBINATION_TOOLTIP = "Default Algorithm combination";
	public static final String DEFAULT_CODE_TOOLTIP = "This is the preview of the code, that will be generated into your Java project";
	public static final String DEFAULT_CHECKBOX_TOOLTIP = "If this checkbox is checked, the code for the above algorithm \nwill be generated into your java project after clicking 'Finish'";
	public static final String ALGORITHM_COMBO_TOOLTIP = "The algorithm combinations are listed in a decreasing order of security level";
	public static final String INSTANCE_DETAILS_TOOLTIP = "Details of the selected algorithm combination";

	//Decoration
	public static final String DEFAULT_ALGORITHM_CHECKBOX_ENABLE = "If you want to view other possible algorithm combinations \nmatching your requirements, please uncheck and click 'Next'";
	public static final String DEFAULT_ALGORITHM_CHECKBOX_DISABLE = "There are no other algorithm combinations matching your requirements.\nThe code for the above algorithm will be generated into your java project";
	public static final String DEFAULT_ALGORITHM_NOTIFICATION = "This algorithm was presented to you previously,\n as the best algorithm combination.";
	public static final String GUIDED_MODE_CHECKBOX_INFO = "If you do not use the guided mode, then you have to \nconfigure the algorithm by yourself";

	// if the next question page depends on user input, the Page object encodes this as a nextPageID as opposed to the one that the last page points to
	public static final int QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID = -2;

	// the last page points to this virtual nextPageID
	public static final int QUESTION_PAGE_LAST_PAGE_NEXT_ID = -1;

	// the answer does not point to a next page, so in this case the page links to a next one statically
	public static final int ANSWER_NO_NEXT_ID = -2;

	// the given answer makes the wizard end
	public static final int ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID = -1;

	// Clafer Instance Generation
	public static final String claferPath = Constants.rsrcPath + "ClaferModel" + Constants.innerFileSeparator + "SymmetricEncryption.js";
	public static final String PATH_FOR_CONFIG_XML = "/Configurator.xml";
	public static final String XML_FILE_NAME = Constants.rsrcPath + "ClaferModel/Encrypt_CryptoTasks.xml";

	// Input for Code Generation
	public static final String pathToXSLFile = Constants.rsrcPath + "XSLTemplates" + Constants.innerFileSeparator + "JCA.xsl";
	public static final String pathToClaferInstanceFolder = Constants.rsrcPath + "ClaferInstance" + Constants.innerFileSeparator;
	public static final String pathToClaferInstanceFile = "claferInstance.xml";
	public static final String pathToClaferPreviewFile = "claferPreview.xml";
	public static final String NameOfTemporaryMethod = "templateUsage";
	public static final String pathsForLibrariesInDevProject = "libs";
	public static final String AuthorTag = "@author CogniCrypt";

	// Output of Code Generation
	public static final String AdditionalOutputFile = "Output.java";
	public static final String AdditionalOutputTempFile = "OutputTemp.java";
	public static final String TempSuffix = "Temp";
	public static final String PackageName = "Crypto";
	public static final String CodeGenerationCallFile = Constants.innerFileSeparator + Constants.PackageName + Constants.innerFileSeparator + Constants.AdditionalOutputFile;

	// File info for Code Generation
	public static final String OpenFile = "Current file is open: ";
	public static final String CloseFile = "No file is open";
	public static final String ContainsAuthorTag = "Current open file contains \"" + AuthorTag + "\": ";
	public static final String ContainsNotAuthorTag = "Current open file DOESN'T contain \"" + AuthorTag + "\": ";
	public static final String CreateOutput = "Create: " + AdditionalOutputFile;
	public static final String CreateOutputTemp = AdditionalOutputFile + " exists! Create: " + AdditionalOutputTempFile;

	// Error Messages
	public static final String NoFileOpenedErrorMessage = "There is no file opened to generate the source code in. Will generate output file instead.";
	public static final String NoJavaFileOpenedErrorMessage = "The currently open file is not a java file. Will generate output file instead.";
	public static final String CodePreviewErrorMessage = "An error occured during code preview generation.";
	public static final String TransformerErrorMessage = "An error occured while performing a transformation ";
	public static final String TransformerConfigurationErrorMessage = "An error occured during creation of transformer";
	public static final String WritingInstanceClaferErrorMessage = "An error occured during";
	public static final String CodeGenerationErrorMessage = "An error occured during template generation.";
	public static final String FilesDoNotExistErrorMessage = "At least one of the files necessary for template generation does not exist.";
	public static final String NoRunMethodFoundInTemporaryOutputFileErrorMessage = "XSL Template does not contain method " + Constants.NameOfTemporaryMethod + ".";
	public static final String NoTemporaryOutputFile = "Temporary output file does not exist.";
	public static final String NoFileandNoProjectOpened = "No file opened and no project selected.";
	public static final String NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE = "No possible combinations are available for chosen values. Please modify your preferences and try agin.\n \n You can use  \n>= insted of >\n<= instead of <\nto make your selection generic.";
	public static final String NO_POSSIBLE_COMBINATIONS_BEGINNER = "No possible combinations are available for chosen values. Please modify your preferences and try agin.";
	public static final String PLEASE_SELECT = "Please select project directory to launch the configurator";
	public static final String ERROR_MESSAGE_NO_PROJECT = "There is no Java project in your workspace. Please create one and restart CogniCrypt.";
	public static final String ERROR_MESSAGE_NO_ADDITIONAL_RES_DIRECTORY = "No directory for additional resources found.";
	public static final String ERROR_MESSAGE_NO_FILE = "No file found";

	public static final String JAVA = "java";
	public static final String DEFAULT_PROVIDER = "JCA";
	public static final String JAR = ".jar";
	public static final String ALGORITHM = "algorithm";
	public static final String GUIDED_MODE = "Use the guided mode for configuring the task";
	public static final String DEFAULT_ALGORITHM_PAGE_CHECKBOX = "I like to generate the code for the default algorithm into my Java project";

	public static final String Package = "Package";
	public static final String Description = "description";
	public static final String Imports = "Imports";
	public static final String Import = "Import";
	public static final String Task = "task";
	public static final String Code = "code";
	public static final String Type = "type";
	public static final String Security = "security";
	public static final String[] xmlimportsarr = { "java.security.InvalidAlgorithmParameterException", "java.security.InvalidKeyException", "java.security.NoSuchAlgorithmException", "java.security.NoSuchAlgorithmException", "javax.crypto.SecretKey", "javax.crypto.BadPaddingException", "javax.crypto.Cipher", "javax.crypto.IllegalBlockSizeException", "javax.crypto.NoSuchPaddingException", "java.security.SecureRandom", "javax.crypto.spec.IvParameterSpec", "javax.crypto.spec.SecretKeySpec", "java.security.spec.InvalidKeySpecException", "java.util.List", "java.util.Base64", "java.io.InputStream", "java.io.OutputStream", "java.util.Properties", "java.io.FileOutputStream" };

	// Types
	public static final String INTEGER = "Integer";
	public static final String STRING = "String";
	public static final String BOOLEAN = "Boolean";

	public static final int INT_HIGH = 600;
	public static final int INT_LOW = -17;
	public static final String INSTANCE_DETAILS = "Instance Details";
	public static final String CODE_PREVIEW = "Code Preview";
	public static final String ARIAL = "Arial";
	public static final String SELECT_JAVA_PROJECT = "Select a Java Project :";
	public static final String SELECT_TASK = "Select a Task :";
	public static final String TASK_DESCRIPTION = "Task Description :";
	public static final String NO_XML_INSTANCE_FILE_TO_WRITE = "No xml instance file to write.";
	public static final String CLAFER_ALGORITHM = "_Algorithm";

	public static final String ALGORITHM_SELECTION_PAGE = "Algorithm Selection Page";
	public static final String DEFAULT_ALGORITHM_PAGE = "Default Algorithm Page";
	public static final String DESCRIPTION_INSTANCE_LIST_PAGE = "Available algorithm combinations matching your requirements are listed below";
	public static final String DESCRIPTION_DEFAULT_ALGORITHM_PAGE = "Best algorithm combination and the code matching your requirements is shown below";
	public static final String instanceList = "Select an algorithm combination";
	public static final String defaultAlgorithm = "Algorithm combination:";
	public static final String TASK_LIST = "Select Task";
	public static final String DESCRIPTION_TASK_SELECTION_PAGE = "Which cryptography task would you like to perform?";
	public static final String DESCRIPTION_VALUE_SELECTION_PAGE = "The following questions help to find the algorithm configuration most suited for your needs";
	public static final String PROPERTIES = "Algorithm Preferences";
	public static final String SELECT_PROPERTIES = "Select Properties";

	//Flags for default project selection
	public static boolean WizardActionFromContextMenuFlag = false;

	public static final String NO_RES_FOUND = "No resource to generate error marker for found.";
	public static final String OBJECT_OF_TYPE = "Object of type ";
	public static final String VAR = "Variable ";

}
