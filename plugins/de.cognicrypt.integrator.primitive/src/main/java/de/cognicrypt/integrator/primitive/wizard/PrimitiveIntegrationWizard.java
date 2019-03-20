/********************************************************************************
 * Copyright (c) 2015-2018 TU Darmstadt
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.primitive.wizard;

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
import de.cognicrypt.codegenerator.question.Page;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.primitive.clafer.ClaferGenerator;
import de.cognicrypt.integrator.primitive.providerUtils.Helper;
import de.cognicrypt.integrator.primitive.providerUtils.ProviderFile;
import de.cognicrypt.integrator.primitive.providerUtils.UserJavaProject;
import de.cognicrypt.integrator.primitive.providerUtils.XsltWriter;
import de.cognicrypt.integrator.primitive.types.Primitive;
import de.cognicrypt.integrator.primitive.wizard.questionnaire.PrimitiveQuestionnaire;
import de.cognicrypt.integrator.primitive.wizard.questionnaire.PrimitiveQuestionnairePage;
import de.cognicrypt.utils.Utils;

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

	@Override
	public void addPages() {
		this.selectedPrimitivePage = new PrimitiveSelectionPage();
		setForcePreviousAndNextButtons(true);
		addPage(this.selectedPrimitivePage);

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

	private void createPrimitivePage(final Page curPage, final PrimitiveQuestionnaire primitiveQuestionnaire, final int iteration) {
		final List<String> selection = null;
		if (curPage.getContent().size() == 1) {
			final Question curQuestion = curPage.getContent().get(0);
			System.out.print(curQuestion.getId());
		}
		// Pass the questionnaire instead of the all of the questions.
		this.preferenceSelectionPage = new PrimitiveQuestionnairePage(curPage, this.primitiveQuestions.getPrimitive(), primitiveQuestionnaire, selection, iteration);
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage currentPage) {
		this.selectedPrimitive = this.selectedPrimitivePage.getSelectedPrimitive();
		if (currentPage == this.selectedPrimitivePage && this.selectedPrimitivePage.isPageComplete()) {
			this.primitiveQuestions = new PrimitiveQuestionnaire(this.selectedPrimitive, this.selectedPrimitive.getXmlFile());
			this.preferenceSelectionPage = new PrimitiveQuestionnairePage(this.primitiveQuestions.nextPage(), this.primitiveQuestions.getPrimitive(), null);
			if (this.preferenceSelectionPage != null) {
				addPage(this.preferenceSelectionPage);

			}

			return this.preferenceSelectionPage;
		} else if (currentPage.getPreviousPage() == this.selectedPrimitivePage || currentPage instanceof PrimitiveQuestionnairePage) {
			final PrimitiveQuestionnairePage primitiveQuestionPage = (PrimitiveQuestionnairePage) currentPage;
			final LinkedHashMap<String, String> selectionMap = primitiveQuestionPage.getMap();

			if (primitiveQuestionPage.getSelection() != null) {
				for (final String name : selectionMap.keySet()) {

					final String key = name.toString();
					final String value = selectionMap.get(name).toString();

					this.inputsMap.put(key, value);

				}
			}

			if (this.primitiveQuestions.hasMorePages()) {
				int nextID = -1;
				if (primitiveQuestionPage.getPageNextID() > -2) {
					nextID = primitiveQuestionPage.getPageNextID();
					setPageId(this.primitiveQuestions.getCurrentPageID());
				}

				if (nextID > -1) {
					final Page curPage = this.primitiveQuestions.setPageByID(nextID);
					setPageId(this.primitiveQuestions.getCurrentPageID());
					createPrimitivePage(curPage, this.primitiveQuestions, primitiveQuestionPage.getIteration());
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

	private void setPageId(final int pageId) {
		this.pageId = pageId;

	}

	public String getPageId() {
		//incrementing the page id to get the correct page number
		final int pageNumber = this.pageId + 2;
		final String pageNumberText = "-" + Integer.toString(pageNumber) + "-";
		return pageNumberText;

	}

	//Adding page numbers to the window
	@Override
	public String getWindowTitle() {
		if (getContainer() != null) {
			final IWizardPage currentPage = getContainer().getCurrentPage();
			if (currentPage == this.selectedPrimitivePage) {
				return "-1-";
			} else if (currentPage == this.preferenceSelectionPage) {
				return (getPageId());
			} else if (currentPage == this.projectBrowserPage) {
				return "-7-";
			} else if (currentPage == this.methodSelectionPage) {
				return "-8-";
			}
		}

		return "Primitive Integration";
	}

	@Override
	public boolean performFinish() {

		//Clafer
		final File finalClafer = ClaferGenerator.copyClaferHeader(Constants.claferHeader, Constants.claferFooter);
		ClaferGenerator.printClafer(this.inputsMap, finalClafer);

		//Generation of xml file for xsl
		final File xmlFile = Utils.getResourceFromWithin(Constants.xmlFilePath);
		this.xsltWriter = new XsltWriter();
		try {
			this.xsltWriter.createDocument();
			this.xsltWriter.setRoot("SymmetricBlockCipher");
			for (final String name : this.inputsMap.keySet()) {
				name.toString();
				final String value = this.inputsMap.get(name).toString();
				this.xsltWriter.addElement(name.trim(), value);
			}
			this.xsltWriter.transformXml(xmlFile);
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

		//Code generation
		try {
			final File templateSpi = Utils.getResourceFromWithin(this.selectedPrimitive.getXslFile());
			final File templateMaster = Utils.getResourceFromWithin(
				Constants.primitivesPath + Constants.innerFileSeparator + "XSL" + Constants.innerFileSeparator + "Template" + Constants.innerFileSeparator + "providerClass.xsl");

			this.xsltWriter.transformXsl(templateSpi, xmlFile);
			this.xsltWriter.transformXsl(templateMaster, xmlFile);
		} catch (TransformerException | SAXException | IOException | ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		//Store source code of generated classes into a map
		final File folder = Utils.getResourceFromWithin(Constants.primitivesPath);
		this.classContent = new Helper().getSourceCode(folder);
		for (final String name : this.classContent.keySet()) {
			final String className = name.toString();
			final String sourceCode = this.classContent.get(name).toString();

			//Create new class that contains the source code
			final UserJavaProject project = this.methodSelectionPage.getUserProject();
			try {
				this.providerName = this.inputsMap.get("name");
				project.createNewClass(className, sourceCode, project.getPackageByName(Constants.PRIMITIVE_PACKAGE));

				//Create provider jarFile
				this.provider.zipProject(project.getProject().getLocation().toString() + "/",
					new File(Utils.getResourceFromWithin(Constants.PROVIDER_FOLDER) + Constants.innerFileSeparator + this.providerName + ".jar"), true);
				//delete archived files
				for (final File file : folder.listFiles()) {
					if (file.getName().endsWith(".java") || file.getName().endsWith(".class")) {
						file.delete();
					}
				}

				//add delete Project
				//project.deleteProject();
			} catch (final JavaModelException e) {
				e.printStackTrace();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

}
