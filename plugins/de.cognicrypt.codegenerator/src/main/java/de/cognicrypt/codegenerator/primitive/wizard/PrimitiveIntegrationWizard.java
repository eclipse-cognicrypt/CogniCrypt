/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.primitive.wizard;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.xml.sax.SAXException;

import de.cognicrypt.codegenerator.primitive.clafer.ClaferGenerator;
import de.cognicrypt.codegenerator.primitive.providerUtils.Helper;
import de.cognicrypt.codegenerator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.codegenerator.primitive.providerUtils.UserJavaProject;
import de.cognicrypt.codegenerator.primitive.providerUtils.XsltWriter;
import de.cognicrypt.codegenerator.primitive.types.Primitive;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnaire;
import de.cognicrypt.codegenerator.primitive.wizard.questionnaire.PrimitiveQuestionnairePage;
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.codegenerator.utilities.CodeGenUtils;
import de.cognicrypt.core.Constants;

public class PrimitiveIntegrationWizard extends Wizard {

	PrimitiveSelectionPage selectedPrimitivePage;
	PrimitiveQuestionnairePage primitiveQuestionsPage;
	PrimitiveQuestionnaire primitiveQuestions;
	JavaProjectBrowserPage projectBrowserPage;
	MethodSelectorPage methodSelectionPage;
	WizardPage preferenceSelectionPage;
	LinkedHashMap<String, String> inputsMap = new LinkedHashMap<String, String>();
	LinkedHashMap<String, String> classContent = new LinkedHashMap<String, String>();
	String providerName;
	XsltWriter xsltWriter;
	ProviderFile provider = new ProviderFile();
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
		File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooter);
		ClaferGenerator.printClafer(inputsMap, finalClafer);

		//Generation of xml file for xsl
		final File xmlFile = CodeGenUtils.getResourceFromWithin(Constants.xmlFilePath);
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
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

		//Code generation 
		try {
			final File templateSpi = CodeGenUtils.getResourceFromWithin(selectedPrimitive.getXslFile());
			final File templateMaster = CodeGenUtils.getResourceFromWithin(
				Constants.primitivesPath + Constants.innerFileSeparator + "XSL" + Constants.innerFileSeparator + "Template" + Constants.innerFileSeparator + "providerClass.xsl");

			xsltWriter.transformXsl(templateSpi, xmlFile);
			xsltWriter.transformXsl(templateMaster, xmlFile);
		} catch (TransformerException | SAXException | IOException | ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		//Store source code of generated classes into a map
		File folder = CodeGenUtils.getResourceFromWithin(Constants.primitivesPath);
		classContent = new Helper().getSourceCode(folder);
		for (String name : classContent.keySet()) {
			String className = name.toString();
			String sourceCode = classContent.get(name).toString();

			//Create new class that contains the source code 
			UserJavaProject project = this.methodSelectionPage.getUserProject();
			try {
				providerName = inputsMap.get("name");
				project.createNewClass(className, sourceCode, project.getPackageByName(Constants.PRIMITIVE_PACKAGE));

				//Create provider jarFile 
				provider.zipProject(project.getProject().getLocation().toString() + "/",
					new File(CodeGenUtils.getResourceFromWithin(Constants.PROVIDER_FOLDER) + Constants.innerFileSeparator + providerName + ".jar"), true);
				//delete archived files 
				for (File file : folder.listFiles()) {
					if (file.getName().endsWith(".java") || file.getName().endsWith(".class"))
						file.delete();
				}

				//add delete Project
				//project.deleteProject();
			} catch (JavaModelException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

}
