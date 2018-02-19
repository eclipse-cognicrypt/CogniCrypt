package de.cognicrypt.codegenerator;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

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
	public static final String ContainsAuthorTag = "Current open file contains \""+AuthorTag+"\": ";
	public static final String ContainsNotAuthorTag = "Current open file DOESN'T contain \""+AuthorTag+"\": ";
	public static final String CreateOutput = "Create: "+AdditionalOutputFile;
	public static final String CreateOutputTemp = AdditionalOutputFile+" exists! Create: "+AdditionalOutputTempFile;

	
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
	public static final String ERROR = "ERROR: ";
	public static final String ERROR_MESSAGE_BLANK_FILE_NAME = "Please choose a valid file.";
	public static final String ERROR_MESSAGE_UNABLE_TO_READ_FILE = "There is a problem with the selected file. Please choose a valid one.";
	public static final String ERROR_MESSAGE_DUPLICATE_TASK_NAME = "A task with this name already exists.";
	public static final String MESSAGE_REQUIRED_FIELD = "This is a required field.";
	public static final String ERROR_MESSAGE_BLANK_TASK_NAME = "The Task name cannot be empty. Please enter a valid name for the Task.";
	

	
	
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
	public static final String[] xmlimportsarr = { "java.security.InvalidAlgorithmParameterException", "java.security.InvalidKeyException", "java.security.NoSuchAlgorithmException", "java.security.NoSuchAlgorithmException", "javax.crypto.SecretKey", "javax.crypto.BadPaddingException", "javax.crypto.Cipher", "javax.crypto.IllegalBlockSizeException", "javax.crypto.NoSuchPaddingException", "java.security.SecureRandom", "javax.crypto.spec.IvParameterSpec", "javax.crypto.spec.SecretKeySpec", "java.security.spec.InvalidKeySpecException", "java.util.List", "java.util.Base64", "java.io.InputStream", "java.io.OutputStream"};

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
	
	// Constants for the Task Integrator Wizard.
	// Page constants
	
	public static final String PAGE_NAME_FOR_MODE_OF_WIZARD = "pageForChoiceOfModeOfWizard";
	public static final String PAGE_TITLE_FOR_MODE_OF_WIZARD = "Please select the mode for the wizard";
	public static final String PAGE_DESCRIPTION_FOR_MODE_OF_WIZARD = "Here you can update the basic details of the task, and the mode in which the wizard will run.";
	
	public static final String PAGE_NAME_FOR_CLAFER_FILE_CREATION = "pageForClaferFileCreation";
	public static final String PAGE_TITLE_FOR_CLAFER_FILE_CREATION = "Add the variablity modelling here";
	public static final String PAGE_DESCRIPTION_FOR_CLAFER_FILE_CREATION = "Here you can add features to the variability model here.";
	
	public static final String PAGE_NAME_FOR_XSL_FILE_CREATION = "pageForXSLFileCreation";
	public static final String PAGE_TITLE_FOR_XSL_FILE_CREATION = "Add data for the code generation";
	public static final String PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION = "Here you can details for the code generation.";
	
	public static final String PAGE_NAME_FOR_HIGH_LEVEL_QUESTIONS = "pageForHighLevelQuestions";
	public static final String PAGE_TITLE_FOR_HIGH_LEVEL_QUESTIONS = "Add the high level questions and their dependencies here";
	public static final String PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS = "Here you can add the questions that will be asked to the end user, and the dependencies to the "+
																			"variability modelling and the code generation.";
	
	public static final String PAGE_NAME_FOR_LINK_ANSWERS = "pageForLinkAnswers";
	public static final String PAGE_TITLE_FOR_LINK_ANSWERS = "Select the question that needs to be linked to another question";
	public static final String PAGE_DESCIPTION_FOR_LINK_ANSWERS = "Here you can configure which question should be displayed next upon selection of a particular answer of the current question"+
																	"by clicking the Link Answer button.";
	
	// Widget constants
	// Labels
	public static final String LABEL_BROWSE_BUTTON = "Browse";
	// Dimensions
	public static final int UI_WIDGET_HEIGHT_NORMAL = 29;
	// Constants for the composites
	public static final String WIDGET_DATA_NAME_OF_THE_TASK = "NameOfTheTask";
	public static final String WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK = "Location of the Library file";
	public static final String WIDGET_DATA_LOCATION_OF_CLAFER_FILE = "Location of the Clafer file";
	public static final String WIDGET_DATA_LOCATION_OF_XSL_FILE = "Location of the XSL file";
	public static final String WIDGET_DATA_LOCATION_OF_JSON_FILE = "Location of the JSON file";
	public static final String WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED = "isCustomLibraryRequired";
	public static final String WIDGET_DATA_IS_GUIDED_MODE_CHOSEN = "isGuidedModeChosen";
	public static final String WIDGET_DATA_IS_GUIDED_MODE_FORCED = "isGuidedModeForced";
	
	// Constants for the drop down for the library type on the mode selection page
	public static final String WIDGET_CONTENT_EXISTING_LIBRARY = "No custom Library";
	public static final String WIDGET_CONTENT_CUSTOM_LIBRARY = "Custom Library";
	
	//Constants for the drop down for the xsl tag on the xsl code page
	public static final String XSL_VARIABLE_TAG="xslVariable";
	public static final String XSL_SELECT_TAG="select";
	public static final String XSL_IF_TAG ="if";
	public static final String XSL_RESULT_DOCUMENT="result-document";
	public static final String XSL_APPLY_TEMPLATES ="apply-templates";
	public static final String XSL_CHOOSE_TAG="choose";
	public static final String XSL_WHEN_TAG="when";
	public static final String XSL_OTHERWISE_TAG="otherwise";
	
	// Default bounds for the composites
	public static final Point DEFAULT_SIZE_FOR_TI_WIZARD = new Point(1050, 600);
	public static final Rectangle RECTANGLE_FOR_COMPOSITES = new Rectangle(0, 0, 887, 500 - 10 -10 ); //897 - 10
	public static final Rectangle RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES = new Rectangle(897, 10, 100, 29); //1000 - 83 - 10 -10
	public static final Rectangle RECTANGLE_FOR_SECOND_BUTTON_FOR_NON_MODE_SELECTION_PAGES = new Rectangle(897, 49, 100, 29); //1000 - 83 - 10 -10
	//public static final Rectangle RECTANGLE_FOR_GRANULAR_CLAFER_UI_ELEMENT = new Rectangle(10, 10, 744, 280);
	public static final int WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT = 744;
	public static final int HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT = 280;
	
	public static final int SINGLE_LINE_TEXT_BOX_LIMIT = 256;
	public static final int MULTI_LINE_TEXT_BOX_LIMIT = 2560;
	
	// Form  data for CompositeGranularUIForClaferFeature
	public static final int RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT = 736;
	
	public static final int PADDING_BETWEEN_GRANULAR_UI_ELEMENTS = 10;
	public static final int PADDING_BETWEEN_SMALLER_UI_ELEMENTS = 3;
	
	public static final int WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT = 409;
	public static final int HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT = 37;
	
	// TypeOfTargetDataForSmallUIElements int values. Add the type of data that needs to be shown on the composite here.
	public static final int FEATURE_PROPERTY = 0;
	public static final int FEATURE_CONSTRAINT = 1;
	
	// source for the contents of this enum : https://www.w3.org/TR/xslt20/
	public static enum XSLTags{
		XSL_VARIABLE_TAG("Variable","<xsl:variable>","</xsl:variable>", new String[]{"name",
																					"select",
																					"as"
																					/*,
																					 * "static",
																					 * "visibility"*/
																					}),
		XSL_VALUE_OF_TAG("Value of","<xsl:value-of/>","", new String[]{"select", "separator" , "disable-output-escaping"}),
		XSL_IF_TAG("If","<xsl:if>","</xsl:if>",new String[]{"test"}),
		XSL_RESULT_DOCUMENT("Result Document","<xsl:result-document>","</xsl:result-document>", new String[]{"href",
																											"format",
																											"validation",
																											"type",
																											"method",
																											"byte-order-mark",
																											"cdata-section-elements",
																											"doctype-public",
																											"doctype-system",
																											"encoding",
																											"escape-uri-attributes",
																											"include-content-type",
																											"indent",
																											"media-type",
																											"normalization-form",
																											"omit-xml-declaration",
																											"standalone",
																											"undeclare-prefixes",
																											"use-character-maps",
																											"output-version"
																											/*,
																											"allow-duplicate-names",
																											"build-tree",
																											"html-version",
																											"item-separator",
																											"json-node-output-method",
																											"parameter-document",
																											"suppress-indentation",*/																									
																											}),
		XSL_APPLY_TEMPLATES("Apply Templates","<xsl:apply-templates />","", new String[]{"select",
																						"mode"
																							}),
		XSL_CHOOSE_TAG("Choose","<xsl:choose>","</xsl:choose>", new String[]{}),
		XSL_WHEN_TAG("When","<xsl:when test =\"\">","</xsl:when>", new String[]{"select"
																				}),
		XSL_OTHERWISE_TAG("Otherwise","<xsl:otherwise>","</xsl:otheriwse>", new String[]{});
		
		private final String XSLTagFaceName;
		private final String XSLBeginTag;
		private final String XSLEndTag;
		private final String[] XSLAttributes;
		/**
		 * @param XSLTagFaceNameParam
		 * @param XSLBeginTagParam
		 * @param XSLEndTagParam
		 */
		private XSLTags(String XSLTagFaceNameParam, String XSLBeginTagParam, String XSLEndTagParam, String[] XSLAttributesParam) {
			XSLTagFaceName = XSLTagFaceNameParam;
			XSLBeginTag = XSLBeginTagParam;
			XSLEndTag = XSLEndTagParam;
			XSLAttributes = XSLAttributesParam;
		}
		/**
		 * @return the xSLTagFaceName
		 */
		public String getXSLTagFaceName() {
			return XSLTagFaceName;
		}
		/**
		 * @return the xSLBeginTag
		 */
		public String getXSLBeginTag() {
			return XSLBeginTag;
		}
		/**
		 * @return the xSLEndTag
		 */
		public String getXSLEndTag() {
			return XSLEndTag;
		}
		/**
		 * @return the xSLAttributes
		 */
		public String[] getXSLAttributes() {
			return XSLAttributes;
		}
		
		
	}
	
	public static enum FeatureConstraintRelationship{
		EQUAL("="),
		NOTEQUAL("!="),
		LESSTHAN("<"),
		GREATERTHAN(">"),
		LESSTHANEQUALTO("<="),
		GREATERTHANEQUALTO(">="),
		AND("and"),
		OR("or");
		
		private final String operatorValue;
		
		FeatureConstraintRelationship(String operatorValue){
			this.operatorValue = operatorValue;
		}

		/**
		 * @return the operatorValue
		 */
		public String getOperatorValue() {
			return operatorValue;
		}

		/* (non-Javadoc)
		 * @see java.lang.Enum#toString()
		 * Just return the operator value instead of the name.
		 */
		@Override
		public String toString() {			
			return  operatorValue;
		}
		
		
	}
	
	public static enum FeatureType{
		CONCRETE,
		ABSTRACT;

		/* 
		 * Although "concrete" is never used in the actual cfr file, "abstract" is used. Hence the toString() returns lower case.
		 */
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
		
	}
	
	public static final String DOUBLE_SLASH = "//";
	public static final String ATTRIBUTE_BEGIN = "/[@";
	public static final String ATTRIBUTE_END = "=\'\']";
	public static final String DOT = ".";

	// for the file utils for the task integrator.
	public static final String JAR_FILE_DIRECTORY_PATH = rsrcPath + "AdditionalResources" + innerFileSeparator;
	public static final String CFR_FILE_DIRECTORY_PATH = rsrcPath + "ClaferModel" + innerFileSeparator;
	public static final String JSON_FILE_DIRECTORY_PATH = rsrcPath + "TaskDesc" + innerFileSeparator;
	public static final String XSL_FILE_DIRECTORY_PATH = rsrcPath + "XSLTemplates" + innerFileSeparator;
	public static final String CFR_EXTENSION = ".cfr";
	public static final String JS_EXTENSION = ".js";
	public static final String JAR_EXTENSION = ".jar";
	public static final String JSON_EXTENSION = ".json";
	public static final String XSL_EXTENSION = ".xsl";
	
	//constants to be used for creation of json file
	public static final String openSquareBracket = "[";
	public static final String closeSquareBracket = "]";
	public static final String openCurlyBrace = "{";
	public static final String closeCurlyBrace = "}";
	public static final String commaOperator =",";
	public static final String colonOperator = ":";
	public static final String quotationMark = "\"";
	public static final String taskIDField = "id";
	public static final String taskIDValue = "0";
	public static final String helpIDField = "helpID";
	public static final String contentFieldName = "content";
	public static final String qstnIDField = "id";
	public static final String elementField = "element";
	public static final String noteField = "note";
	public static final String qstnTxtField = "question Text";
	public static final String answersField = "answers";
	public static final String valueField = "value";
	public static final String claferDependenciesField = "claferDependencies";
	public static final String algorithmField = "algorithm";
	public static final String operandField = "operand";
	public static final String operatorField = "operator";
	public static final String defaultAnswerField = "defaultAnswer";
	public static final String nextIDField = "nextID";
	public static final String codeDependenciesField = "codeDependencies";
	public static final String optionField = "option";

	public static final String FEATURE_PROPERTY_TYPE_RELATION = "is of type";
	public static final String FEATURE_PROPERTY_NAME = "Name";
	public static final String FEATURE_PROPERTY_REMOVE = "Remove";
	
	
	
	public static final String[] CLAFER_RESERVED_WORDS = { "abstract", "all", "assert", "disj", "else", "enum", "if", "in", "lone", "max", "maximize", "min", "minimize", "mux", "no", "not", "one", "opt", "or", "product", "some", "sum", "then", "xor" };
	public static final String[] CLAFER_PRIMITIVE_TYPES = { "integer", "double", "real", "string" };
	
	
	
}
