package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.generator.XSLBasedGenerator;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.primitive.utilities.WriteXML;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnaire;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnairePage;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.Utils;

public class PrimitiveIntegrationWizard extends Wizard {

	PrimitiveSelectionPage selectedPrimitivePage;
	PrimitiveQuestionnaire primitiveQuestions;
	JavaProjectBrowserPage projectBrowserPage;
	MethodSelectorPage methodSelectionPage;
	WizardPage preferenceSelectionPage;
	private LinkedHashMap<String, String> inputsMap = new LinkedHashMap<String, String>();
	StringBuilder data = new StringBuilder();
	WriteXML xmlFileForXSL;

	public PrimitiveIntegrationWizard() {
		super();
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
		}
		// Pass the questionnaire instead of the all of the questions. 
		this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curPage, this.primitiveQuestions.getPrimitive(), primitiveQuestionnaire, selection, iteration);
	}

	public IWizardPage getNextPage(final IWizardPage currentPage) {
		final Primitive selectedPrimitive = this.selectedPrimitivePage.getSelectedPrimitive();
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
					System.out.println(key + " " + value);
					inputsMap.put(key, value);
					System.out.println("combien?");

				}
			}

			if (this.primitiveQuestions.hasMorePages()) {
				int nextID = -1;
				if (primitiveQuestionPage.getPageNextID() > -2) {
					nextID = primitiveQuestionPage.getPageNextID();
				}

				if (nextID > -1) {
					final Page curPage = this.primitiveQuestions.setPageByID(nextID);
					System.out.println(primitiveQuestions.getCurrentPageID());
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
			
			
			this.methodSelectionPage = new MethodSelectorPage(this.projectBrowserPage.getSelectedFile());
			addPage(this.methodSelectionPage);
			return this.methodSelectionPage;
		}

		return currentPage;
	}

	//	public IWizardPage getPreviousPage(final IWizardPage currentPage) {
	//		final boolean lastPage = currentPage instanceof lastPage;
	//		if (!checkifInUpdateRound() && currentPage instanceof PrimitiveQuestionnairePage || lastPage) {
	//			if (!this.primitiveQuestions.isFirstPage()) {
	//				this.primitiveQuestions.previousPage();
	//			}
	//		}
	//		return super.getPreviousPage(currentPage);
	//	}

	@Override
	public boolean performFinish() {

		//Generation of xml file for xsl
		File xmlFile =new File("C:\\Users\\Ahmed\\issues\\CogniCrypt\\plugins\\de.cognicrypt.codegenerator\\src\\main\\resources\\Primitives\\xmlFile.xml"); //Change local
		xmlFileForXSL = new WriteXML();
		try{
			xmlFileForXSL.createDocument();
			xmlFileForXSL.setRoot("SymmetricBlockCipher");
			for (String name : inputsMap.keySet()) {

				String key = name.toString();
				String value = inputsMap.get(name).toString();
				xmlFileForXSL.addElement(name.trim(), value);
				System.out.println(name + value);
		

			}
			xmlFileForXSL.isCreated(xmlFile);
			System.out.println(xmlFile.getAbsolutePath() + " and " + xmlFile.getName());
		}
		catch(ParserConfigurationException | TransformerException e){
			e.printStackTrace();
		}
		File xslFile=new File("C:\\Users\\Ahmed\\issues\\CogniCrypt\\plugins\\de.cognicrypt.codegenerator\\src\\main\\resources\\Primitives\\XSL\\testxsl.xsl");
		System.out.println("Le fichier existe ?"+xslFile.exists());
		//Code generation 
		
		return true;
	}

	public boolean performCancel() {
		boolean ans = MessageDialog.openConfirm(getShell(), "Confirmation", "Are you sure to close without integrating the new primitve?");
		if (ans)
			return true;
		else
			return false;
	}
}