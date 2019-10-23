/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.wizard;

import java.util.HashMap;
import java.util.List;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import de.cognicrypt.codegenerator.question.CodeDependency;
import de.cognicrypt.codegenerator.question.Question;
import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.controllers.XSLPageContentProvider;
import de.cognicrypt.integrator.task.controllers.XSLPageLabelProvider;
import de.cognicrypt.integrator.task.controllers.XSLStringGenerationAndManipulation;
import de.cognicrypt.integrator.task.models.ClaferFeature;
import de.cognicrypt.integrator.task.models.ClaferModel;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;
import de.cognicrypt.integrator.task.widgets.CompositeForXsl;

public class XslPage extends PageForTaskIntegratorWizard {

	private CompositeForXsl compositeForXsl = null;

	private HashMap<String, String> tagValueTagData;

	public XslPage() {
		super(Constants.PAGE_NAME_FOR_XSL_FILE_CREATION, Constants.PAGE_TITLE_FOR_XSL_FILE_CREATION, Constants.PAGE_DESCRIPTION_FOR_XSL_FILE_CREATION);

		// The String to display, and the constructed string for the XSL document.
		setTagValueTagData(new HashMap<>());
	}

	public static Object[] mergeLists(final Object[] firstList, final Object[] secondList) {
		final Object[] mergedList = new Object[firstList.length + secondList.length];
		for (int i = 0; i < firstList.length; i++) {
			mergedList[i] = firstList[i];
		}
		for (int i = 0; i < secondList.length; i++) {
			mergedList[i + firstList.length] = secondList[i];
		}

		return mergedList;
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		setControl(container);

		// make the page layout two-column
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(new GridLayout(2, false));

		setCompositeForXsl(new CompositeForXsl(container, SWT.NONE));
		// fill the available space on the with the big composite
		final GridData gdXSLComposite = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gdXSLComposite.heightHint = 0;
		getCompositeForXsl().setLayoutData(gdXSLComposite);

		final Button btnAddXSLTag = new Button(container, SWT.PUSH);// Add button to add the xsl tag in the code
		btnAddXSLTag.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnAddXSLTag.setText("Add Xsl Tag");

		final Button btnReadCode = new Button(container, SWT.PUSH);// Add button to add the xsl tag in the code
		btnReadCode.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		btnReadCode.setText("Get the code");

		btnReadCode.addSelectionListener(new SelectionAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */

			@Override
			public void widgetSelected(final SelectionEvent e) {

				super.widgetSelected(e);

				if (getCompositeForXsl().getXslTxtBox().getText().trim().length() > 0) {
					final MessageBox infoBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
					infoBox.setText("Updating code");
					infoBox.setMessage(
							"Some code already appears to be added. \n\nIf you choose an XSL file, all of the existing code will be replaced. If you choose a Java or text file, the contents of said file will be added at the location of the cursor.");
					infoBox.open();
				}
				final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);

				fileDialog.setFilterExtensions(new String[] {"*.xsl", "*.java", "*.txt"});
				fileDialog.setText("Choose the code file:");

				final String fileDialogResult = fileDialog.open();
				if (fileDialogResult != null) {
					getCompositeForXsl().updateTheTextFieldWithFileData(fileDialogResult);
				}

			}

		});

		btnAddXSLTag.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final int scrollbarPosY = getCompositeForXsl().getXslTxtBox().getTopPixel();

				// this is needed to get the name and the description of the task from the wizard.
				final ModelAdvancedMode objectForDataInGuidedMode =
						((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_MODE_OF_WIZARD)).getCompositeChoiceForModeOfWizard().getObjectForDataInNonGuidedMode();
				final String taskName = objectForDataInGuidedMode.getNameOfTheTask();
				final String taskDescription = objectForDataInGuidedMode.getTaskDescription();

				// Get the path for the javascript file from the clafer page.
				final String jsFilePath = ((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getJSFilePath();
				// Get the questions from the earlier pages.
				final List<Question> questions =
						((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS)).getCompositeToHoldGranularUIElements().getListOfAllQuestions();

				XSLStringGenerationAndManipulation.getListOfValidSuggestionsForXSLTags(jsFilePath, taskName, taskDescription, questions, getTagValueTagData());
				XSLTagDialog dialog;
				// Show an empty dialog if no clafer feature has been defined.
				if (getTagValueTagData().size() > 0) {
					dialog = new XSLTagDialog(getShell(), getTagValueTagData());
				} else {
					dialog = new XSLTagDialog(getShell());
				}

				if (dialog.open() == Window.OK) {
					// To locate the position of the xsl tag to be introduced in the code.
					final Point selected = getCompositeForXsl().getXslTxtBox().getSelection();
					String xslTxtBoxContent = getCompositeForXsl().getXslTxtBox().getText();
					xslTxtBoxContent = xslTxtBoxContent.substring(0, selected.x) + dialog.getTag().toString() + xslTxtBoxContent.substring(selected.y, xslTxtBoxContent.length());
					getCompositeForXsl().getXslTxtBox().setText(xslTxtBoxContent);
					getCompositeForXsl().colorizeTextBox();
					getCompositeForXsl().getXslTxtBox().setTopPixel(scrollbarPosY);
				}

			}
		});

		this.treeViewer = new TreeViewer(container);

		this.treeViewer.setContentProvider(new XSLPageContentProvider());
		this.treeViewer.setLabelProvider(new XSLPageLabelProvider());

		setTreeViewerInput();
		final GridData gdTreeViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdTreeViewer.heightHint = 0;
		this.treeViewer.getControl().setLayoutData(gdTreeViewer);

		this.treeViewer.getControl().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				if (XslPage.this.treeViewer.getSelection() instanceof TreeSelection) {
					final TreeSelection ts = (TreeSelection) XslPage.this.treeViewer.getSelection();

					// add feature name to XSL code if feature name clicked
					if (ts.getFirstElement() instanceof ClaferFeature) {
						final ClaferFeature featureClicked = (ClaferFeature) ts.getFirstElement();

						insertAtCursor(featureClicked.getFeatureName());
					} else if (ts.getFirstElement() instanceof CodeDependency) {
						final CodeDependency codeDependency = (CodeDependency) ts.getFirstElement();

						final StringBuilder sb = new StringBuilder();
						sb.append("//task/code/");
						sb.append(codeDependency.getOption());
						sb.append("='");
						sb.append(codeDependency.getValue());
						sb.append("'");

						insertAtCursor(sb.toString());
					}
				}
				super.mouseDoubleClick(e);
			}
		});
	}

	public void setTreeViewerInput() {
		final ClaferModel inputClafer =
				(((TaskIntegrationWizard) getWizard()).getTIPageByName(Constants.PAGE_NAME_FOR_CLAFER_FILE_CREATION)).getCompositeToHoldGranularUIElements().getClaferModel();

		final List<Question> questions =
				((PageForTaskIntegratorWizard) getWizard().getPage(Constants.PAGE_NAME_FOR_LINK_ANSWERS)).getCompositeToHoldGranularUIElements().getListOfAllQuestions();

		final Object[] treeViewerInput = new Object[] {inputClafer, questions};

		this.treeViewer.setInput(treeViewerInput);
		this.treeViewer.refresh();
	}

	/**
	 * Return the composite for the XSL page.
	 *
	 * @return the compositeForXsl
	 */
	public CompositeForXsl getCompositeForXsl() {
		return this.compositeForXsl;
	}

	/**
	 * The composite is maintained as a global variable to have access to it as part of the page object.
	 *
	 * @param compositeForXsl the compositeForXsl to set
	 */
	private void setCompositeForXsl(final CompositeForXsl compositeForXsl) {
		this.compositeForXsl = compositeForXsl;

	}

	/**
	 * @return the tagValueTagData
	 */
	public HashMap<String, String> getTagValueTagData() {
		return this.tagValueTagData;
	}

	/**
	 * @param tagValueTagData the tagValueTagData to set
	 */
	public void setTagValueTagData(final HashMap<String, String> tagValueTagData) {
		this.tagValueTagData = tagValueTagData;
	}

	/**
	 * insert the given {@link String} text into the text box
	 *
	 * @param text {@link String} to be placed at the cursor position
	 */
	private void insertAtCursor(final String text) {
		final Point selection = getCompositeForXsl().getXslTxtBox().getSelection();
		String xslTxtBoxContent = getCompositeForXsl().getXslTxtBox().getText();
		xslTxtBoxContent = xslTxtBoxContent.substring(0, selection.x) + text + xslTxtBoxContent.substring(selection.y, xslTxtBoxContent.length());
		getCompositeForXsl().getXslTxtBox().setText(xslTxtBoxContent);

		getCompositeForXsl().colorizeTextBox();
		// place cursor behind the inserted text
		getCompositeForXsl().getXslTxtBox().setSelection(selection.x + text.length());
		getCompositeForXsl().getXslTxtBox().setFocus();
	}

}
