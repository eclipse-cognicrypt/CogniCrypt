package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.xml.sax.SAXException;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.codegenerator.primitive.providerUtils.XsltWriter;
import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnaire;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnairePage;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.Utils;

public class PrimitiveIntegrationWizard extends Wizard {

	PrimitiveSelectionPage selectedPrimitivePage;
	PrimitiveQuestionnairePage primitiveQuestionsPage;
	PrimitiveQuestionnaire primitiveQuestions;
	JavaProjectBrowserPage projectBrowserPage;
	MethodSelectorPage methodSelectionPage;
	WizardPage preferenceSelectionPage;
	private LinkedHashMap<String, String> inputsMap = new LinkedHashMap<String, String>();
	StringBuilder data = new StringBuilder();
	XsltWriter xsltWriter;
	ProviderFile providerJar = new ProviderFile("Test");
	Primitive selectedPrimitive;
	int pageId;

	public PrimitiveIntegrationWizard() {
		super();
		//Add page number to window title
		setWindowTitle(getWindowTitle());
	}

	public void addPages() {
		selectedPrimitivePage = new PrimitiveSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(selectedPrimitivePage);

	}

	private boolean checkifInUpdateRound() {
		boolean updateRound = false;
		final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		for (final StackTraceElement el : stack) {
			if (el.getMethodName().contains("updateButtons")) {
				updateRound = true;
				break;
			}
		}
		return updateRound;
	}

	private void createPrimitivePage(final Page curPage, final PrimitiveQuestionnaire primitiveQuestionnaire, int iteration) {
		List<String> selection = null;
		if (curPage.getContent().size() == 1) {
			final Question curQuestion = curPage.getContent().get(0);
			System.out.print(curQuestion.getId());
		}
		// Pass the questionnaire instead of the all of the questions. 
		this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curPage, this.primitiveQuestions.getPrimitive(), primitiveQuestionnaire, selection, iteration);
	}

	public IWizardPage getNextPage(final IWizardPage currentPage) {
		selectedPrimitive = this.selectedPrimitivePage.getSelectedPrimitive();
		if (currentPage == this.selectedPrimitivePage && this.selectedPrimitivePage.isPageComplete()) {

			this.primitiveQuestions = new PrimitiveQuestionnaire(selectedPrimitive, selectedPrimitive.getXmlFile());
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(this.primitiveQuestions.nextPage(), this.primitiveQuestions.getPrimitive(), null);
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);

			}

			return this.preferenceSelectionPage;
		} else if (currentPage.getPreviousPage() == this.selectedPrimitivePage || currentPage instanceof PrimitiveQuestionnairePage) {
			final PrimitiveQuestionnairePage primitiveQuestionPage = (PrimitiveQuestionnairePage) currentPage;
			LinkedHashMap<String, String> selectionMap = primitiveQuestionPage.getMap();

			if (primitiveQuestionPage.getSelection() != null) {
				for (String name : selectionMap.keySet()) {

					String key = name.toString();
					String value = selectionMap.get(name).toString();

					inputsMap.put(key, value);

				}
			}

			if (this.primitiveQuestions.hasMorePages()) {
				int nextID = -1;
				if (primitiveQuestionPage.getPageNextID() > -2) {
					nextID = primitiveQuestionPage.getPageNextID();
					setPageId(primitiveQuestions.getCurrentPageID());
				}

				if (nextID > -1) {
					final Page curPage = this.primitiveQuestions.setPageByID(nextID);
					setPageId(primitiveQuestions.getCurrentPageID());
					createPrimitivePage(curPage, primitiveQuestions, primitiveQuestionPage.getIteration());
					if (checkifInUpdateRound()) {
						this.primitiveQuestions.previousPage();
					}
					final IWizardPage[] pages = getPages();
					for (int i = 1; i < pages.length; i++) {
						if (!(pages[i] instanceof PrimitiveQuestionnairePage)) {
							continue;
						}
						final PrimitiveQuestionnairePage oldPage = (PrimitiveQuestionnairePage) pages[i];

						if (oldPage.equals(this.preferenceSelectionPage)) {

							return oldPage;
						}
					}
					if (this.preferenceSelectionPage != null) {

						addPage(this.preferenceSelectionPage);

					}
					return this.preferenceSelectionPage;
				} else {
					this.projectBrowserPage = new JavaProjectBrowserPage("test");
					addPage(this.projectBrowserPage);
					return this.projectBrowserPage;
				}
			}
		}

		else if (currentPage instanceof JavaProjectBrowserPage) {

			this.methodSelectionPage = new MethodSelectorPage(this.projectBrowserPage.getAbsolutePath());
			addPage(this.methodSelectionPage);
			return this.methodSelectionPage;
		}

		return currentPage;
	}

	private void setPageId(int pageId) {
		this.pageId = pageId;

	}

	public String getPageId() {
		//incrementing the page id to get the correct page number
		int pageNumber = this.pageId + 2;
		String pageNumberText = "-" + Integer.toString(pageNumber) + "-";
		return pageNumberText;

	}

	//Adding page numbers to the window
	@Override
	public String getWindowTitle() {
		if (getContainer() != null) {
			IWizardPage currentPage = getContainer().getCurrentPage();
			if (currentPage == selectedPrimitivePage)
				return "-1-";
			else if (currentPage == preferenceSelectionPage)
				return (getPageId());
			else if (currentPage == projectBrowserPage)
				return "-7-";
			else if (currentPage == methodSelectionPage)
				return "-8-";
		}

		return "Primitive Integration";

	}

	@Override
	public boolean performFinish() {
		//Clafer
		File finalClafer = ClaferGenerator.copyClaferHeader();
				ClaferGenerator.printClafer(inputsMap, finalClafer);
			
		//Generation of xml file for xsl
		final File xmlFile = Utils.getResourceFromWithin(Constants.xmlFilePath);
		xsltWriter = new XsltWriter();
		try {
			xsltWriter.createDocument();
			xsltWriter.setRoot("SymmetricBlockCipher");
			for (String name : inputsMap.keySet()) {
				String key = name.toString();
				String value = inputsMap.get(name).toString();
				xsltWriter.addElement(name.trim(), value);
			}
			xsltWriter.transformXml(xmlFile);
		}
			catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
			}
		
		
		//Code generation 
		final File xslFile = Utils.getResourceFromWithin(selectedPrimitive.getXslFile());
		try {
			//			File temporaryOutputFile=new File();
			//			transform(xmlFile, xslFile,temporaryOutputFile.getPath());
			xsltWriter.transformXsl(xslFile, xmlFile);

		} catch (TransformerException | SAXException | IOException | ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		//Generation of .class files from the transformed .java files
		File folder = Utils.getResourceFromWithin(Constants.transformedFiles);
		File[] listOfFiles = (folder).listFiles();
		for (File file : listOfFiles) {
			System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_131");
			System.out.println(System.getProperty("java.home"));
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

			Iterable<? extends JavaFileObject> compilationUnits1 = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
			compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();
		}

		//Create Provider jarFile 
		String[] classPaths = { "com/java/Cipher.class", "com/java/Provider.class" };
		providerJar.createManifest("some owner", classPaths);
		
		providerJar.createJarArchive(Utils.getResourceFromWithin(Constants.PROVIDER_JAR_File),folder.listFiles());
		
		//delete archived files 
		for(File file: folder.listFiles()){
			file.delete();
		}
		
		return true;
	}
			
		
}


	//		public boolean canFinish() {
	//			final String pageName = getContainer().getCurrentPage().getName();
	//			if (pageName.equals(Constants.METHODS_SELECTION_PAGE)) { //name of the last page
	//				return true;
	//			}
	//			return (pageName.equals(Constants.METHODS_SELECTION_PAGE));
	//		}

//	public boolean performCancel() {
//		boolean ans = MessageDialog.openConfirm(getShell(), "Confirmation", "Are you sure to close without integrating the new primitve?");
//		if (ans)
//			return true;
//		else
//			return false;
//	}
