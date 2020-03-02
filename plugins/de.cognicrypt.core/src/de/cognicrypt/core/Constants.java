/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.core;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * This class comprises all constants that are used by the plugin.
 */
public class Constants {

	public enum GUIElements {
		combo, text, textarea, button, radio, checkbox, composed, rbtextgroup
	}

	public enum CodeGenerators {
		XSL, CrySL
	}
	
	public enum Rules {
		JavaCryptographicArchitecture, BouncyCastle, Tink
	}
	
	public enum Severities {
		Error, Warning, Info, Ignored;

		public static Severities get(int i) {
			return values()[i];
		}
	}

	public static enum CG {
		CHA, SPARK_LIBRARY, SPARK
	}

	public static final String NO_RES_FOUND = "No resource to generate error marker for found.";
	public static final String OBJECT_OF_TYPE = "Object of type ";
	public static final String VAR = "Variable ";

	// The plugin is bundled in a jar archive and the file separator within jar
	// files is / (see:
	// https://stackoverflow.com/questions/24749007/how-to-use-file-separator-for-a-jar-file-resource).
	// Use this file separator for all paths within the plugin space.
	public static final String innerFileSeparator = "/";

	// Use this file separator for all paths outside the plugin space.
	public static final String outerFileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");

	public static final String JavaNatureID = "org.eclipse.jdt.core.javanature";
	public static final String MavenNatureID = "org.eclipse.m2e.core.maven2Nature";

	public static final String rsrcPath = "src" + Constants.innerFileSeparator + "main" + Constants.innerFileSeparator + "resources" + Constants.innerFileSeparator;
	public static final String providerPath = Constants.rsrcPath + "AdditionalResources" + Constants.innerFileSeparator + "Provider";

	// JSON task file
	public static final String jsonTaskFile = Constants.rsrcPath + "Tasks" + Constants.innerFileSeparator + "tasks.json";

	public static final String pathToPropertyfiles = Constants.rsrcPath + "Labels.properties";

	public static final String jsonPrimitiveTypesFile = rsrcPath + "Primitives" + innerFileSeparator + "JSON" + innerFileSeparator + "PrimitiveType.json";

	// XML & XSL for Primitive
	public static final String primitivesPath = Constants.rsrcPath + "Primitives";
	public static final String xmlFilePath = primitivesPath + innerFileSeparator + "XML" + innerFileSeparator + "xmlFile.xml";
	public static final String cipherSpiXSL = primitivesPath + innerFileSeparator + "XSL" + innerFileSeparator + "Template" + innerFileSeparator + "CipherSPI.xsl";
	public static final String providerClassXSL = primitivesPath + innerFileSeparator + "XSL" + innerFileSeparator + "Template" + innerFileSeparator + "providerClass.xsl";
	public static final String testPrimitverFolder = "src/test/resources/PrimitiveIntegration/";

	// Added Packages
	public static final String PRIMITIVE_PACKAGE = "de.cognicrypt.customPrimitive";

	// Clafer related file
	public static final String claferHeader = primitivesPath + innerFileSeparator + "Clafer" + innerFileSeparator + "ClaferHeader.cfr";
	public static final String claferHeaderTest = testPrimitverFolder + innerFileSeparator + "Clafer" + innerFileSeparator + "ClaferHeader.cfr";
	public static final String claferFooter = primitivesPath + innerFileSeparator + "Clafer" + innerFileSeparator + "";
	public static final String claferFooterTest = testPrimitverFolder + innerFileSeparator + "Clafer" + innerFileSeparator + "FinalClaferT.cfr";
	public static final String claferFooterTest2 = testPrimitverFolder + innerFileSeparator + "Clafer" + innerFileSeparator + "FinalClaferTe.cfr";
	public static final String claferFooterTest3 = testPrimitverFolder + innerFileSeparator + "clafer" + innerFileSeparator + "FinalClaferTes.cfr";
	public static final String claferHeaderTestR = testPrimitverFolder + innerFileSeparator + "Clafer" + innerFileSeparator + "ClaferHeader.cfr";
	public static final String claferFooterTestR = testPrimitverFolder + innerFileSeparator + "Clafer" + innerFileSeparator + "FinalClafer.cfr";
	// Jar file location - Provider (Primitive)
	public static final String PROVIDER_FOLDER = rsrcPath + "AdditionalResources" + innerFileSeparator + "Provider" + innerFileSeparator;

	// Task descriptions

	// Tooltip
	public static final String PROJECTLIST_TOOLTIP = "List of your Java projects";
	public static final String TASKLIST_TOOLTIP = "Cryptographic tasks supported by CogniCrypt";
	public static final String DESCRIPTION_BOX_TOOLTIP = "Here is the description for the cryptographic task that you have selected";
	public static final String GUIDEDMODE_TOOLTIP = "Guided mode configures the algorithm for you,\nbased on your answers to some simple questions.";
	public static final String DEFAULT_ALGORITHM_COMBINATION_TOOLTIP = "Default Algorithm combination";
	public static final String DEFAULT_CODE_TOOLTIP = "This is the preview of the code, that will be generated into your Java project";
	public static final String DEFAULT_CHECKBOX_TOOLTIP =
			"If you want to view other possible algorithm combinations \nmatching your requirements, please check this box and click 'Next'";
	public static final String ALGORITHM_COMBO_TOOLTIP = "The algorithm combinations are listed in a decreasing order of security level";
	public static final String INSTANCE_DETAILS_TOOLTIP = "Details of the selected algorithm combination";
	public static final String PREVIOUS_ALGORITHM_BUTTON = "Previous";
	public static final String NEXT_ALGORITHM_BUTTON = "Next";
	public static final String PORT_NUMBER_TOOLTIP = "443 HTTPS\n 22 SSH";
	public static final String IP_ADDRESS_TOOLTIP = "Example: 255.255.255.255";

	// Decoration
	public static final String DEFAULT_ALGORITHM_CHECKBOX_ENABLE =
			"If this checkbox is unchecked, the code for the above algorithm \nwill be generated into your java project after clicking 'Finish'";
	public static final String DEFAULT_ALGORITHM_CHECKBOX_DISABLE =
			"There are no other algorithm combinations matching your requirements.\nThe code for the above algorithm will be generated into your java project on clicking 'Finish'";
	public static final String DEFAULT_ALGORITHM_NOTIFICATION = "This algorithm was presented to you previously, as the best algorithm combination.";
	public static final String GUIDED_MODE_CHECKBOX_INFO = "If you do not use the guided mode, then you have to \nconfigure the algorithm by yourself";
	public static final String COMPARE_SAME_ALGORITHM = "The variations selected in both dropdowns are same. Please modify your selection.";

	// if the next question page depends on user input, the Page object encodes this
	// as a nextPageID as opposed to the one that the last page points to
	public static final int QUESTION_PAGE_NO_STATIC_NEXT_PAGE_ID = -2;

	// the last page points to this virtual nextPageID
	public static final int QUESTION_PAGE_LAST_PAGE_NEXT_ID = -1;

	// the answer does not point to a next page, so in this case the page links to a
	// next one statically
	public static final int ANSWER_NO_NEXT_ID = -2;

	// the given answer makes the wizard end
	public static final int ANSWER_NO_FOLLOWING_QUESTION_NEXT_ID = -1;

	// Input for Code Generation
	public static final String pathToXSLFile = Constants.rsrcPath + "XSLTemplates" + Constants.innerFileSeparator + "JCA.xsl";
	public static final String pathToClaferInstanceFolder = Constants.rsrcPath + "ClaferInstance" + Constants.innerFileSeparator;
	public static final String claferPath = Constants.rsrcPath + "ClaferModel" + Constants.innerFileSeparator + "Encryption.js";
	public static final String PATH_FOR_CONFIG_XML = "/Configurator.xml";
	public static final String XML_FILE_NAME = Constants.rsrcPath + "ClaferModel/Encrypt_CryptoTasks.xml";

	public static final String pathToClaferInstanceFile = "claferInstance.xml";
	public static final String pathToClaferPreviewFile = "claferPreview.xml";
	public static final String NameOfTemporaryMethod = "templateUsage";
	public static final String pathsForLibrariesInDevProject = "libs";
	public static final String AuthorTag = "@author CogniCrypt";

	// Output of Code Generation
	public static final String AdditionalOutputFile = "Output.java";
	public static final String AdditionalOutputTempFile = "OutputTemp.java";
	public static final String TempSuffix = "Temp";

	public static final String PackageName = "de" + Constants.outerFileSeparator + "cognicrypt" + Constants.outerFileSeparator + "crypto";
	public static final String PackageNameAsName =
			PackageName.replaceAll(("\\".equals(Constants.outerFileSeparator) ? Constants.outerFileSeparator : "") + Constants.outerFileSeparator, ".");
	public static final String CodeGenerationCallFolder = Constants.innerFileSeparator + Constants.PackageName;
	public static final String CodeGenerationCallFile = CodeGenerationCallFolder + Constants.innerFileSeparator + Constants.AdditionalOutputFile;

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
	public static final String NO_POSSIBLE_COMBINATIONS_ARE_AVAILABLE =
			"No possible combinations are available for chosen values. Please modify your preferences and try n.\n \n You can use  \n>= insted of >\n<= instead of <\nto make your selection generic.";
	public static final String NO_POSSIBLE_COMBINATIONS_BEGINNER = "No possible combinations are available for chosen values. Please modify your preferences and try again.";
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

	public static final String NOT_JAVA_PROJECT = "The nature of the project is not Java";

	public static final String JAVA = "java";
	public static final String DEFAULT_PROVIDER = "JCA";
	public static final String JAR = ".jar";
	public static final String ALGORITHM = "algorithm";
	public static final String GUIDED_MODE = "Use the guided mode for configuring the task";
	public static final String DEFAULT_ALGORITHM_PAGE_CHECKBOX = "Show other possible algorithm combinations";
	public static final String SHOW_PASSWORD_CHECKBOX = "Show Password";

	public static final String Package = "Package";
	public static final String Description = "description";
	public static final String Imports = "Imports";
	public static final String Import = "Import";
	public static final String Task = "task";
	public static final String Code = "code";
	public static final String Type = "type";
	public static final String Security = "Security";
	public static final String Performance = "Performance";
	public static final String[] xmlimportsarr = {"java.security.InvalidAlgorithmParameterException", "java.security.InvalidKeyException", "java.security.NoSuchAlgorithmException",
			"java.security.NoSuchAlgorithmException", "javax.crypto.SecretKey", "javax.crypto.BadPaddingException", "javax.crypto.Cipher", "javax.crypto.IllegalBlockSizeException",
			"javax.crypto.NoSuchPaddingException", "java.security.SecureRandom", "javax.crypto.spec.IvParameterSpec", "javax.crypto.spec.SecretKeySpec",
			"java.security.spec.InvalidKeySpecException", "java.util.List", "java.util.Base64", "java.io.InputStream", "java.io.OutputStream", "java.util.Properties",
			"java.io.FileOutputStream", "java.security.Key", "java.net.URL", "java.io.File", "javax.net.ssl.HttpsURLConnection", "java.security.Signature"};

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
	public static final String instanceList = "Select an algorithm combination: ";
	public static final String defaultAlgorithm = "Algorithm combination: ";
	public static final String TASK_LIST = "Task Selection";
	public static final String DESCRIPTION_TASK_SELECTION_PAGE = "Select the cryptography task that you like to perform. ";
	public static final String DESCRIPTION_VALUE_SELECTION_PAGE = "The following questions help to find the algorithm configuration most suited for your needs";
	public static final String PROPERTIES = "Algorithm Preferences: ";
	public static final String SELECT_PROPERTIES = "Select Properties";
	public static final String FIXED_SIZE = "fixed size";
	public static final String BLOCK_SIZE = "blocksize";
	public static final String METHODS_SELECTION_PAGE = "Methods Selector";
	public static final String DESCRIPTION_KEYSIZES = "The keysize can fixed or variable sized";

	// Compare algorithm page
	public static final String COMPARE_ALGORITHM_PAGE = "Compare Algorithms Page";
	public static final String COMPARE_TITLE = "Compare Two Algorithms";
	public static final String COMPARE_DESCRIPTION = "Select the algorithms that you wish to compare. Their properties will be shown in the corresponding boxes below.";
	public static final String COMPARE_LABEL = "Instance details of";
	public static final String LABEL_COMPARE_ALGORITHMS_BUTTON = "Compare Algorithms";

	// Code Preview Page
	public static final String CODE_PREVIEW_PAGE = "Code Preview Page";
	public static final String CODE_PREVIEW_PAGE_TITLE = "Code preview for the selected solution ";
	public static final String CODE_PREVIEW_PAGE_DESCRIPTION = "The preview of the code that will be generated in your Java project is shown below.";
	public static final String LABEL_CODE_PREVIEW_BUTTON = "Code Preview";

	// Text type
	public static final String BROWSE = "Browse";
	public static final String PASSWORD = "Password";
	public static final String PORT_NUMBER = "Port number";
	public static final String IP_ADDRESS = "Ip address";
	// Flags for default project selection
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
	public static final String PAGE_DESCRIPTION_FOR_HIGH_LEVEL_QUESTIONS =
			"Here you can add the questions that will be asked to the end user, and the dependencies to the " + "variability modelling and the code generation.";

	public static final String PAGE_NAME_FOR_LINK_ANSWERS = "pageForLinkAnswers";
	public static final String PAGE_TITLE_FOR_LINK_ANSWERS = "Select the question that needs to be linked to another question";
	public static final String PAGE_DESCIPTION_FOR_LINK_ANSWERS =
			"Here you can configure which question should be displayed next upon selection of a particular answer of the current question" + "by clicking the Link Answer button.";

	// Widget constants
	// Labels
	public static final String LABEL_BROWSE_BUTTON = "Browse";
	// Dimensions
	public static final int UI_WIDGET_HEIGHT_NORMAL = 29;
	// Constants for the composites
	public static final String WIDGET_DATA_NAME_OF_THE_TASK = "NameOfTheTask";
	public static final String WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK = "Location of the Library";
	public static final String WIDGET_DATA_LIBRARY_LOCATION_OF_THE_HELP_FILE = "Location of the Help file";
	public static final String WIDGET_DATA_LOCATION_OF_CLAFER_FILE = "Location of the Clafer file";
	public static final String WIDGET_DATA_LOCATION_OF_XSL_FILE = "Location of the XSL file";
	public static final String WIDGET_DATA_LOCATION_OF_JSON_FILE = "Location of the JSON file";
	public static final String WIDGET_DATA_LOCATION_OF_HELP_FILE = "Location of the Help file";
	public static final String WIDGET_DATA_IS_CUSTOM_LIBRARY_REQUIRED = "isCustomLibraryRequired";
	public static final String WIDGET_DATA_IS_GUIDED_MODE_CHOSEN = "isGuidedModeChosen";
	public static final String WIDGET_DATA_IS_GUIDED_MODE_FORCED = "isGuidedModeForced";

	// Constants for the drop down for the library type on the mode selection page
	public static final String WIDGET_CONTENT_EXISTING_LIBRARY = "No custom Library";
	public static final String WIDGET_CONTENT_CUSTOM_LIBRARY = "Custom Library";

	// Constants for the drop down for the xsl tag on the xsl code page
	public static final String XSL_VARIABLE_TAG = "xslVariable";
	public static final String XSL_SELECT_TAG = "select";
	public static final String XSL_IF_TAG = "if";
	public static final String XSL_RESULT_DOCUMENT = "result-document";
	public static final String XSL_APPLY_TEMPLATES = "apply-templates";
	public static final String XSL_CHOOSE_TAG = "choose";
	public static final String XSL_WHEN_TAG = "when";
	public static final String XSL_OTHERWISE_TAG = "otherwise";

	// Default bounds for the composites
	public static final Point DEFAULT_SIZE_FOR_TI_WIZARD = new Point(1050, 600);
	public static final Rectangle RECTANGLE_FOR_COMPOSITES = new Rectangle(0, 0, 887, 500 - 10 - 10); // 897 - 10
	public static final Rectangle RECTANGLE_FOR_FIRST_BUTTON_FOR_NON_MODE_SELECTION_PAGES = new Rectangle(897, 10, 100, 29); // 1000 - 83 - 10 -10
	public static final Rectangle RECTANGLE_FOR_SECOND_BUTTON_FOR_NON_MODE_SELECTION_PAGES = new Rectangle(897, 49, 100, 29); // 1000 - 83 - 10 -10
	// public static final Rectangle RECTANGLE_FOR_GRANULAR_CLAFER_UI_ELEMENT = new
	// Rectangle(10, 10, 744, 280);
	public static final int WIDTH_FOR_GRANULAR_CLAFER_UI_ELEMENT = 744;
	public static final int HEIGHT_FOR_GRANULAR_CLAFER_UI_ELEMENT = 280;

	public static final int SINGLE_LINE_TEXT_BOX_LIMIT = 256;
	public static final int MULTI_LINE_TEXT_BOX_LIMIT = 2560;

	// Form data for CompositeGranularUIForClaferFeature
	public static final int RIGHT_VALUE_FOR_GRANULAR_CLAFER_UI_SUB_ELEMENT = 736;

	public static final int PADDING_BETWEEN_GRANULAR_UI_ELEMENTS = 10;
	public static final int PADDING_BETWEEN_SMALLER_UI_ELEMENTS = 3;

	public static final int WIDTH_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT = 409;
	public static final int HEIGHT_FOR_CLAFER_FEATURE_PROPERTY_UI_ELEMENT = 37;

	// TypeOfTargetDataForSmallUIElements int values. Add the type of data that
	// needs to be shown on the composite here.
	public static final int FEATURE_PROPERTY = 0;
	public static final int FEATURE_CONSTRAINT = 1;

	// source for the contents of this enum : https://www.w3.org/TR/xslt20/
	public static enum XSLTags {
		XSL_VARIABLE_TAG("Variable", "<xsl:variable>", "</xsl:variable>", new String[] {"name", "select", "as"
		/*
		 * , "static", "visibility"
		 */
		}), XSL_VALUE_OF_TAG("Value of", "<xsl:value-of/>", "", new String[] {"select", "separator", "disable-output-escaping"}), XSL_IF_TAG("If", "<xsl:if>", "</xsl:if>",
				new String[] {"test"}), XSL_RESULT_DOCUMENT("Result Document", "<xsl:result-document>", "</xsl:result-document>",
						new String[] {"href", "format", "validation", "type", "method", "byte-order-mark", "cdata-section-elements", "doctype-public", "doctype-system", "encoding",
								"escape-uri-attributes", "include-content-type", "indent", "media-type", "normalization-form", "omit-xml-declaration", "standalone", "undeclare-prefixes",
								"use-character-maps", "output-version"
						/*
						 * , "allow-duplicate-names", "build-tree", "html-version", "item-separator", "json-node-output-method", "parameter-document", "suppress-indentation",
						 */
						}), XSL_APPLY_TEMPLATES("Apply Templates", "<xsl:apply-templates />", "", new String[] {"select", "mode"}), XSL_CHOOSE_TAG("Choose", "<xsl:choose>", "</xsl:choose>",
								new String[] {}), XSL_WHEN_TAG("When", "<xsl:when>", "</xsl:when>",
										new String[] {"test"}), XSL_OTHERWISE_TAG("Otherwise", "<xsl:otherwise>", "</xsl:otherwise>", new String[] {});

		private final String XSLTagFaceName;
		private final String XSLBeginTag;
		private final String XSLEndTag;
		private final String[] XSLAttributes;

		/**
		 * @param XSLTagFaceNameParam
		 * @param XSLBeginTagParam
		 * @param XSLEndTagParam
		 */
		private XSLTags(final String XSLTagFaceNameParam, final String XSLBeginTagParam, final String XSLEndTagParam, final String[] XSLAttributesParam) {
			this.XSLTagFaceName = XSLTagFaceNameParam;
			this.XSLBeginTag = XSLBeginTagParam;
			this.XSLEndTag = XSLEndTagParam;
			this.XSLAttributes = XSLAttributesParam;
		}

		/**
		 * @return the xSLTagFaceName
		 */
		public String getXSLTagFaceName() {
			return this.XSLTagFaceName;
		}

		/**
		 * @return the xSLBeginTag
		 */
		public String getXSLBeginTag() {
			return this.XSLBeginTag;
		}

		/**
		 * @return the xSLEndTag
		 */
		public String getXSLEndTag() {
			return this.XSLEndTag;
		}

		/**
		 * @return the xSLAttributes
		 */
		public String[] getXSLAttributes() {
			return this.XSLAttributes;
		}

	}

	public static enum FeatureConstraintRelationship {
		EQUAL("="), NOTEQUAL("!="), LESSTHAN("<"), GREATERTHAN(">"), LESSTHANEQUALTO("<="), GREATERTHANEQUALTO(">="), AND("and"), OR("or");

		private final String operatorValue;

		FeatureConstraintRelationship(final String operatorValue) {
			this.operatorValue = operatorValue;
		}

		/**
		 * @return the operatorValue
		 */
		public String getOperatorValue() {
			return this.operatorValue;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Enum#toString() Just return the operator value instead of the name.
		 */
		@Override
		public String toString() {
			return this.operatorValue;
		}

	}

	public static enum FeatureType {
		CONCRETE, ABSTRACT;

		/*
		 * Although "concrete" is never used in the actual cfr file, "abstract" is used. Hence the toString() returns lower case.
		 */
		@Override
		public String toString() {
			return name().toLowerCase();
		}

	}

	public static final String SLASH = "/";
	public static final String ATTRIBUTE_BEGIN = "[@";
	public static final String ATTRIBUTE_END = "=\'\']";
	public static final String DOT = ".";

	// for the file utils for the task integrator.
	public static final String JAR_FILE_DIRECTORY_PATH = rsrcPath + "AdditionalResources" + innerFileSeparator;
	public static final String CFR_FILE_DIRECTORY_PATH = rsrcPath + "ClaferModel" + innerFileSeparator;
	public static final String CFR_BIN_FILE_DIRECTORY_PATH = rsrcPath + "ClaferModelBin" + innerFileSeparator;
	public static final String JSON_FILE_DIRECTORY_PATH = rsrcPath + "TaskDesc" + innerFileSeparator;
	public static final String XML_FILE_DIRECTORY_PATH = rsrcPath + "Help" + innerFileSeparator;
	public static final String pluginXmlFile = "plugin.xml";
	public static final String XSL_FILE_DIRECTORY_PATH = rsrcPath + "XSLTemplates" + innerFileSeparator;
	public static final String HELP_FILE_DIRECTORY_PATH = rsrcPath + "Help" + innerFileSeparator;
	public static final String CFR_EXTENSION = ".cfr";
	public static final String CFR_BIN_EXTENSION = ".dat";
	public static final String JS_EXTENSION = ".js";
	public static final String JAR_EXTENSION = ".jar";
	public static final String JSON_EXTENSION = ".json";
	public static final String XML_EXTENSION = ".xml";
	public static final String XSL_EXTENSION = ".xsl";
	public static final String PLUGIN_XML_FILE = innerFileSeparator + "plugin.xml";

	public static final String DEFAULT_FEATURE_SET_FILE = "DefaultFeatureSet";

	public static final String PREDICATEENSURER_GROUPID = "de.upb.cognicrypt.predicateensurer";
	public static final String PREDICATEENSURER_ARTIFACTID = "PredicateEnsurer";
	public static final String PREDICATEENSURER_VERSION = "0.0.1-SNAPSHOT";
	public static final String PREDICATEENSURER_JAR_IMPORT = "de.upb.cognicrypt.predicateensurer.Ensurer";

	public static final String DEPENDENCIES_TAG = "dependencies";
	public static final String DEPENDENCY_TAG = "dependency";
	public static final String GROUPID_TAG = "groupId";
	public static final String ARTIFACTID_TAG = "artifactId";
	public static final String VERSION_TAG = "version";
	public static final String MVN_INSTALL_COMMAND = "install";
	public static final String MVN_CLEAN_COMMAND = "clean";
	public static final String MVN_SKIPTESTS_COMMAND = "-DskipTests=true";
	public static final String MVN_ECLIPSE_COMMAND = "eclipse:eclipse";

	// for the list of items to be included inside the combo of the questionTab
	public static final String dropDown = "Drop down";
	public static final String textBox = "Text box";
	public static final String radioButton = "Radio button";

	// for creating SuppressWarnings.xml
	public static final String SUPPRESSWARNING_FILE = "SuppressWarnings" + XML_EXTENSION;
	public static final String SUPPRESSWARNINGS_ELEMENT = "SuppressWarnings";
	public static final String SUPPRESSWARNING_ELEMENT = "SuppressWarning";
	public static final String ID_ATTR = "ID";
	public static final String FILE_ELEMENT = "File";
	public static final String LINENUMBER_ELEMENT = "LineNumber";
	public static final String MESSAGE_ELEMENT = "Message";

	// QuickFixs
	public static final String SUPPRESSWARNING_FIX = "Suppress Warning: ";
	public static final String UNSUPPRESSWARNING_FIX = "UnSuppress Warning: ";

	// Marker types
	public static final String CC_MARKER_TYPE = "de.cognicrypt.staticanalyzer.ccMarker";
	public static final String FORBIDDEN_METHOD_MARKER_TYPE = "de.cognicrypt.staticanalyzer.forbiddenMethodMarker";
	public static final String IMPRECISE_VALUE_EXTRACTION_MARKER_TYPE = "de.cognicrypt.staticanalyzer.impreciseValueExtractionErrorMarker";
	public static final String PREDICATE_CONTRADICTION_MARKER_TYPE = "de.cognicrypt.staticanalyzer.predicateContradictionErrorMarker";
	public static final String REQUIRED_PREDICATE_MARKER_TYPE = "de.cognicrypt.staticanalyzer.requiredPredicateErrorMarker";
	public static final String CONSTRAINT_ERROR_MARKER_TYPE = "de.cognicrypt.staticanalyzer.constraintErrorMarker";
	public static final String NEVER_TYPEOF_MARKER_TYPE = "de.cognicrypt.staticanalyzer.neverTypeOfErrorMarker";
	public static final String INCOMPLETE_OPERATION_MARKER_TYPE = "de.cognicrypt.staticanalyzer.incompleteOperationErrorMarker";
	public static final String TYPESTATE_ERROR_MARKER_TYPE = "de.cognicrypt.staticanalyzer.typestateErrorMarker";

	public static final String RULE_SELECTION = "de.cognicrypt.staticanalyzer.ruleSelection";
	public static final String AUTOMATED_ANALYSIS = "de.cognicrypt.staticanalyzer.automaticAnalysis";
	public static final String PROVIDER_DETECTION_ANALYSIS = "de.cognicrypt.staticanalyzer.providerDetectionAnalysis";
	public static final String SHOW_SECURE_OBJECTS = "de.cognicrypt.staticanalyzer.secureObjects";
	public static final String CALL_GRAPH_SELECTION = "de.cognicrypt.staticanalyzer.callgraphSelection";
	public static final String SELECT_CUSTOM_RULES = "de.cognicrypt.staticanalyzer.selectCustomRules";

	public static final String ANALYSE_DEPENDENCIES = "de.cognicrypt.staticanalyzer.dependencyAnalysis";

	public static final String PERSIST_CONFIG = "de.cognicrypt.codegenerator.persistConfig";

	// for creating xml file
	public static final String Xml_Declaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static final String NLS_Tag = "<?NLS TYPE=\"org.eclipse.help.contexts\"?>";
	public static final String contextsOpeningTag = "<contexts>";
	public static final String contextsClosingTag = "</contexts>";
	public static final String contextsElement = "contexts";
	public static final String contextElement = "context";
	public static final String idAttribute = "id";
	public static final String titleAttribute = "title";
	public static final String descriptionAttribute = "description";
	public static final String titleAttributeValue = "CogniCrypt";
	public static final String pluginElement = "plugin";
	public static final String extensionElement = "extension";
	public static final String pointAttribute = "point";
	public static final String pointAttributeValue = "org.eclipse.help.contexts";
	public static final String fileAttribute = "file";
	public static final String startingFrom = "src";
	public static final String helpContentNotAvailable = "Help content for this task is not ready. We are sorry!";

	public static final String FEATURE_PROPERTY_TYPE_RELATION = "is of type";
	public static final String FEATURE_PROPERTY_TYPE_REFERENCE_RELATION = "translates to";
	public static final String FEATURE_PROPERTY_NAME = "Name";
	public static final String FEATURE_PROPERTY_REMOVE = "Remove";

	public static final String[] CLAFER_RESERVED_WORDS = {"abstract", "all", "assert", "disj", "else", "enum", "if", "in", "lone", "max", "maximize", "min", "minimize", "mux", "no",
			"not", "one", "opt", "or", "product", "some", "sum", "then", "xor"};
	public static final String[] CLAFER_PRIMITIVE_TYPES = {"integer", "int", "double", "real", "string"};

	public static final String ANALYSIS_LABEL = "CogniCrypt Analysis";

	public static final String BUILDER_ID = "QuickFixTest.ProblemMarkerBuilder";

	public static final String MARKER_TYPE = "QuickFixTest.OCCEProblem";

	// define a correct ID (get range of possible ones)
	public static final int JDT_PROBLEM_ID = 10000000;
	public final static String RELATIVE_RULES_DIR = "resources/CrySLRules";
	public final static String ECLIPSE_RULES_DIR = System.getProperty("user.dir");
	public static final String cryslFileEnding = ".crysl";
	public static final String cryslEditorID = "de.darmstadt.tu.crossing.CrySL";
	public static final String HEALTHY = "Secure";
	public static final String UNHEALTHY = "Insecure";

	// define the max java version before which plugin works.
	public static final String CC_JAVA_VERSION = "1.8";
	
	public static final String RELATIVE_CUSTOM_RULES_DIR = "resources/CrySLRules/Custom";

 	//Preference page rules table Constants
 	public static final String TABLE_HEADER_RULES = "Rules";
 	public static final String TABLE_HEADER_VERSION = "Version";
 	public static final String TABLE_HEADER_URL = "URL";
 	
 	public static final Double MIN_JCA_RULE_VERSION = 1.4;
 	public static final Double MIN_BC_RULE_VERSION = 0.7;
 	public static final Double MIN_TINK_RULE_VERSION = 0.3;
 	public static final Double MIN_BCJCA_RULE_VERSION = 0.2;

 	//Configuration.ini keys
 	public static final String INI_URL_HEADER = "URLS";
 	public static final String INI_NEXUS_SOOT_RELEASE = "NEXUS_SOOT_RELEASE";
 	public static final String INI_JCA_NEXUS = "JCA_NEXUS";
 	public static final String INI_BC_NEXUS = "BC_NEXUS";
 	public static final String INI_TINK_NEXUS = "TINK_NEXUS";
 	public static final String INI_BCJCA_NEXUS = "BCJCA_NEXUS";
 	
 	public final static String CONFIG_FILE_PATH = "configuration.ini";

	// path to icons
	public final static String COGNICRYPT_ICON_DIR = "icons/cognicrypt-analysis.png";
	public static final String codeTemplateFolder =
			"src" + Constants.innerFileSeparator + "main" + Constants.innerFileSeparator + "java" + Constants.innerFileSeparator + "de" + Constants.innerFileSeparator + "cognicrypt"
					+ Constants.innerFileSeparator + "codegenerator" + Constants.innerFileSeparator + "crysl" + Constants.innerFileSeparator + "templates" + Constants.innerFileSeparator;
}
