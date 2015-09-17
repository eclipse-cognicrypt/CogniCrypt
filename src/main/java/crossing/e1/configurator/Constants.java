package crossing.e1.configurator;

/**
 * This class consists of all constants of the plugin.
 * @author Stefan Krueger
 *
 */
public class Constants {
	

	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String lineSeparator = System.getProperty("line.separator");
	public static final String JavaNatureID = "org.eclipse.jdt.core.javanature";

	//Input for Code Generation
	public static final String folderOfXSLTemplates = "resources" + fileSeparator + "XSLTemplates" + fileSeparator + "JCA.xsl";
	public static final String folderOfClaferInstance = "resources/ClaferModel" + fileSeparator +"claferInstance.xml";
	public static final String NameOfTemporaryMethod = "run";
	
	//Output of Code Generation
	public static final String AdditionalOutputFile = "Output.java";
	public static final String Packagname = "crypto";
	public static final String CodeGenerationCallFile = fileSeparator +  Constants.Packagname + fileSeparator + AdditionalOutputFile;
	
	//Strings for GUI elements
	public static final String NewGenerationWizardTitle = "Generate Cryptography Code";
	public static final String NewGenerationWizardPageTitle = "Generate Code";
	
	//Error Messages
	public static final String NoFileOpenedErrorMessage = "There is no file opened to generate the source code in. Will generate output file instead.";
	public static final String NoJavaFileOpenedErrorMessage = "The currently open file is not a java file. Will generate output file instead.";
	public static final String CodeGenerationErrorMessage = "An error occured during template generation.";
	public static final String FilesDoNotExistErrorMessage = "At least one of the files necessary for template generation does not exist.";
	public static final String NoRunMethodFoundInTemporaryOutputFileErrorMessage = "XSL Template does not contain method \"+ NameOfTemporaryMethod + \".";
	public static final String NoTemporaryOutputFile = "Temporary output file does not exist.";
	public static final String NoFileandNoProjectOpened = "No file opened and no project selected.";
	
}
