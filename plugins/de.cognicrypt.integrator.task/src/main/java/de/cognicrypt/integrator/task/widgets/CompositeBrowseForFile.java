/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 

 * http://www.eclipse.org/legal/epl-2.0. SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.integrator.task.widgets;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.hamcrest.core.IsInstanceOf;

import de.cognicrypt.core.Constants;
import de.cognicrypt.integrator.task.UIConstants;
import de.cognicrypt.integrator.task.models.ModelAdvancedMode;
import de.cognicrypt.integrator.task.wizard.PageForTaskIntegratorWizard;

public class CompositeBrowseForFile extends Composite {

	private ModelAdvancedMode objectForDataInNonGuidedMode;
	private PageForTaskIntegratorWizard theLocalContainerPage; // this is needed to set whether the page has been
																// completed yet or not.
	private ControlDecoration decFilePath; // Decoration variable to be able to access it in the events.

	private Listener onFileChangedListener;

	private final Text textBox;
	
	private final Text txtBoxOption;

	public String getTxtBoxOption() {
		return txtBoxOption.getText();
	}

	public CompositeBrowseForFile(final Composite parent, final int style, final String labelText,
			final String[] fileTypes, final String stringOnFileDialog,
			final PageForTaskIntegratorWizard theContainerpageForValidation, final Listener listener) {
		this(parent, style, labelText, fileTypes, stringOnFileDialog, theContainerpageForValidation);
		this.onFileChangedListener = listener;
	}

	public CompositeChoiceForModeOfWizard findAncestor(Composite comp) {
		Composite result = comp;
		while (!(result instanceof CompositeChoiceForModeOfWizard) && result!= null) {
			result = result.getParent();
		}
		if (result instanceof CompositeChoiceForModeOfWizard)
			return (CompositeChoiceForModeOfWizard) result;
		else
			return null;
	}

	/**
	 * Pass the file types that need to be selected, and the string that needs to be
	 * displayed. Pass null in the fileTypes if you wish to select a directory.
	 *
	 * @param parent
	 * @param style
	 * @param labelText
	 * @param fileTypes
	 * @param stringOnDialog
	 * @param theContainerpageForValidation
	 */
	public CompositeBrowseForFile(final Composite parent, final int style, final String labelText,
			final String[] fileTypes, final String stringOnDialog,
			final PageForTaskIntegratorWizard theContainerpageForValidation) {
		super(parent, style);
		// this object is required in the text box listener. Should not be called too
		// often.
		setObjectForDataInNonGuidedMode(findAncestor(getParent()).getObjectForDataInNonGuidedMode());

		setTheLocalContainerPage(theContainerpageForValidation);
		final GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.horizontalSpacing = 8;
		setLayout(gridLayout);

		final Label label = new Label(this, SWT.NONE);
		label.setText(labelText);

		// Initialize the decorator for the label for the text box.
		setDecFilePath(new ControlDecoration(label, SWT.TOP | SWT.RIGHT));
		getDecFilePath().setShowOnlyOnFocus(false);

		// Initial error state.
		getDecFilePath().setImage(UIConstants.DEC_ERROR);
		getDecFilePath().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_BLANK_FILE_NAME);
		getDecFilePath().showHoverText(getDecFilePath().getDescriptionText());

		this.textBox = new Text(this, SWT.BORDER);
		final GridData gdTextBox = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		
		// do not claim space for all the text if not available
		gdTextBox.widthHint = 500;
		this.textBox.setLayoutData(gdTextBox);
		this.txtBoxOption = new Text(this, SWT.BORDER);
		final GridData gdTextBoxOption = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		
		// do not claim space for all the text if not available
		gdTextBoxOption.widthHint = 100;
		this.txtBoxOption.setLayoutData(gdTextBoxOption);
		if(!labelText.equals(Constants.WIDGET_DATA_LOCATION_OF_CRYSLTEMPLATE_FILE)) {
			this.txtBoxOption.setVisible(false);
		}
		final Button browseButton = new Button(this, SWT.NONE);
		browseButton.setText(Constants.LABEL_BROWSE_BUTTON);

		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				String selectedPath = "";
				// If null is passed in the file types, the directory selection dialog will be
				// displayed.
				if (fileTypes == null) {
					selectedPath = openDirectoryDialog(stringOnDialog);
					if (selectedPath != null) {
						CompositeBrowseForFile.this.textBox.setText(selectedPath);
						if (CompositeBrowseForFile.this.onFileChangedListener != null) {
							CompositeBrowseForFile.this.onFileChangedListener.handleEvent(new Event());
						}
					}
				} else {
					selectedPath = openFileDialog(fileTypes, stringOnDialog);
					if (selectedPath != null) {
						CompositeBrowseForFile.this.textBox.setText(selectedPath);
					}
				}

			}
		});

		this.textBox.addModifyListener(e -> {

			File locationOfCryslTemplate = new File(CompositeBrowseForFile.this.textBox.getText());
			final File tempFileVariable = locationOfCryslTemplate;
			// Validate the file IO. The directory check is removed.
			if ((!tempFileVariable.exists() || !tempFileVariable.canRead())
					&& CompositeBrowseForFile.this.textBox.getParent().isVisible()) {//
				getDecFilePath().setImage(UIConstants.DEC_ERROR);
				getDecFilePath().setDescriptionText(Constants.ERROR + Constants.ERROR_MESSAGE_UNABLE_TO_READ_FILE);
				getDecFilePath().showHoverText(getDecFilePath().getDescriptionText());
				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
			} else {
				// If there are no problems with the file, revert the error decoration and store
				// the locations.
				getDecFilePath().setImage(null);
				getDecFilePath().setDescriptionText("");
				getDecFilePath().showHoverText("");
				switch (labelText) {
				case Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_TASK:
					getObjectForDataInNonGuidedMode().setLocationOfCustomLibrary(tempFileVariable);
					break;
				case Constants.WIDGET_DATA_LOCATION_OF_XSL_FILE:
					getObjectForDataInNonGuidedMode().setLocationOfXSLFile(tempFileVariable);
					break;
				case Constants.WIDGET_DATA_LOCATION_OF_JSON_FILE:
					getObjectForDataInNonGuidedMode().setLocationOfJSONFile(tempFileVariable);
					break;
				case Constants.WIDGET_DATA_LOCATION_OF_PNG_FILE:
					getObjectForDataInNonGuidedMode().setLocationOfIconFile(tempFileVariable);
					break;
				case Constants.WIDGET_DATA_LIBRARY_LOCATION_OF_THE_HELP_FILE:
					getObjectForDataInNonGuidedMode().setLocationOfHelpXMLFile(tempFileVariable);
					break;
				}

				// Check if the page can be set to completed.
				getTheLocalContainerPage().checkIfModeSelectionPageIsComplete();
			}
		});
		
		
		this.textBox.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (CompositeBrowseForFile.this.onFileChangedListener != null) {
					CompositeBrowseForFile.this.onFileChangedListener.handleEvent(new Event());
				}
				super.focusLost(e);
			}
		});
	}

	/**
	 * Open the file dialog and return the file path as a string.
	 *
	 * @param fileTypes
	 * @param stringOnFileDialog
	 * @return The path selected
	 */
	private String openFileDialog(final String[] fileTypes, final String stringOnFileDialog) {
		final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(fileTypes);
		fileDialog.setText(stringOnFileDialog);
		return fileDialog.open();
	}

	/**
	 * Open the directorDialog for the custom library, and return the path.
	 *
	 * @param stringOnDirectoryDialog
	 * @return the selected path.
	 */
	private String openDirectoryDialog(final String stringOnDirectoryDialog) {
		final DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), SWT.OPEN);
		directoryDialog.setText(stringOnDirectoryDialog);
		return directoryDialog.open();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * Return the object with the basic data of the task.
	 *
	 * @return the objectForDataInNonGuidedMode
	 */
	private ModelAdvancedMode getObjectForDataInNonGuidedMode() {
		return this.objectForDataInNonGuidedMode;
	}

	/**
	 * This object stores the basic data of the task that is being handled.
	 *
	 * @param objectForDataInNonGuidedMode the objectForDataInNonGuidedMode to set
	 */
	private void setObjectForDataInNonGuidedMode(final ModelAdvancedMode objectForDataInNonGuidedMode) {
		this.objectForDataInNonGuidedMode = objectForDataInNonGuidedMode;
	}

	/**
	 * Return the container wizard page object.
	 *
	 * @return the theLocalContainerPage
	 */
	public PageForTaskIntegratorWizard getTheLocalContainerPage() {
		return this.theLocalContainerPage;
	}

	/**
	 * This object is required to set the completion of the page for the mode
	 * selection page behavior.
	 *
	 * @param theLocalContainerPage the theLocalContainerPage to set
	 */
	public void setTheLocalContainerPage(final PageForTaskIntegratorWizard theLocalContainerPage) {
		this.theLocalContainerPage = theLocalContainerPage;
	}

	public String getText() {
		return this.textBox.getText();
	}

	/**
	 * @return the decNameOfTheTask
	 */
	public ControlDecoration getDecFilePath() {
		return this.decFilePath;
	}

	/**
	 * Keep the decorator object as global to allow access in the event listeners.
	 *
	 * @param decNameOfTheTask the decNameOfTheTask to set
	 */
	private void setDecFilePath(final ControlDecoration decFilePath) {
		this.decFilePath = decFilePath;
	}

}
